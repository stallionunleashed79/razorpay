package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.enums.OrderStatus;
import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.common.exception.BusinessRuleViolationException;
import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.entity.Payment;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentAdapterRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.repository.PaymentRepository;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAdapterRouter paymentAdapterRouter;

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

        final PaymentResult paymentAdapter = paymentAdapterRouter.initiate(
                paymentRequest
        );
        return null;
    }
}
