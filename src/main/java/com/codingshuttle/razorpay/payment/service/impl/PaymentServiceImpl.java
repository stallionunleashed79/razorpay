package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.enums.OrderStatus;
import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.common.exception.BusinessRuleViolationException;
import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.entity.Payment;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentAdapterRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.mapper.PaymentMapper;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.repository.PaymentRepository;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAdapterRouter paymentAdapterRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest paymentInitRequest) {
        final OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(paymentInitRequest.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", paymentInitRequest.orderId()));
        if (orderRecord.getOrderStatus() != OrderStatus.CREATED && orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("Payment can only be initiated " +
                    "for orders in CREATED or ATTEMPTED status.", "ORDER_STATUS_INVALID");
        }
        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(orderRecord)
                .merchantId(merchantId)
                .amount(orderRecord.getAmount())
                .status(PaymentStatus.CREATED)
                .paymentMethod(paymentInitRequest.paymentMethod())
                .methodDetails(paymentInitRequest.methodDetails())
                .build();
        payment = paymentRepository.save(payment);

        final PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(payment.getId())
                .orderId(orderRecord.getId())
                .merchantId(merchantId)
                .amount(orderRecord.getAmount())
                .paymentMethod(paymentInitRequest.paymentMethod())
                .methodDetails(paymentInitRequest.methodDetails())
                .build();

        final PaymentResult paymentResult = paymentAdapterRouter.initiate(
                paymentRequest
        );

        switch (paymentResult) {
            case PaymentResult.Pending pending -> {
                payment.setStatus(PaymentStatus.CREATED);
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                payment.setBankReference(success.bankReference());
                payment.setStatus(PaymentStatus.CREATED);
            }
        }
        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse capture(final UUID merchantId, final UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        payment.setStatus(PaymentStatus.CAPTURING); //TODO - STATE MACHINE IMPLEMENTATION
        final PaymentResult paymentResult = paymentAdapterRouter.capture(payment.getPaymentMethod(),
                paymentId);
        switch (paymentResult) {
            case PaymentResult.Pending pending -> {
                payment.setStatus(PaymentStatus.CAPTURED);
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.AUTHORIZED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
                log.warn("Payment {} captured failed with error code {}", paymentId, failure.errorCode());
            }
            case PaymentResult.Success success -> {
                payment.setBankReference(success.bankReference());
                payment.setStatus(PaymentStatus.CAPTURED);
                payment.setCapturedAt(LocalDateTime.now());
                log.info("Payment {} captured at {}", paymentId, payment.getCapturedAt());
            }
        }
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }
}
