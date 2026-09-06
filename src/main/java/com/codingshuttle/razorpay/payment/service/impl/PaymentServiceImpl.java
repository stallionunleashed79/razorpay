package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest paymentInitRequest) {
        return null;
    }
}
