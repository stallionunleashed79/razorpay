package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.enums.OrderStatus;
import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.common.exception.BusinessRuleViolationException;
import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.entity.Payment;
import com.codingshuttle.razorpay.payment.entity.PaymentEvent;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentAdapterRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.mapper.PaymentMapper;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.repository.PaymentRepository;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import com.codingshuttle.razorpay.payment.service.PaymentTransitionService;
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
    private final PaymentTransitionService paymentTransitionService;

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
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                payment.setBankReference(success.bankReference());
                payment.setStatus(PaymentStatus.CREATED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
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
        payment.setStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
        final PaymentResult paymentResult = paymentAdapterRouter.capture(payment.getPaymentMethod(),
                paymentId);
        switch (paymentResult) {
            case PaymentResult.Pending pending -> {
                payment.setStatus(PaymentStatus.CAPTURED);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.AUTHORIZED);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
                log.warn("Payment {} captured failed with error code {}", paymentId, failure.errorCode());
            }
            case PaymentResult.Success success -> {
                payment.setBankReference(success.bankReference());
                payment.setStatus(PaymentStatus.CAPTURED);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                log.info("Payment {} captured at {}", paymentId, payment.getCapturedAt());
            }
        }
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {
        final Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        if (payment.getStatus() != PaymentStatus.AUTHORIZING) {
            return;
        }

        final OrderRecord orderRecord = payment.getOrder();
        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //Auto-capture flow
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            final PaymentResult captureResult = paymentAdapterRouter.capture(payment.getPaymentMethod(), paymentId);
            if (captureResult instanceof PaymentResult.Success(String bankReference)) {
                payment.setBankReference(bankReference);
                payment.setStatus(PaymentStatus.CAPTURED);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof PaymentResult.Failure(String code, String description)) {
                payment.setStatus(PaymentStatus.AUTHORIZED);
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(code);
                payment.setErrorDescription(description);
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }
        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
    }
}
