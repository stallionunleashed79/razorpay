package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    @Override
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest paymentInitRequest) {
        final OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(paymentInitRequest.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", paymentInitRequest.orderId()));
    }
}
