package com.codingshuttle.razorpay.payment.service;

import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest paymentInitRequest);

    @Nullable PaymentResponse capture(UUID merchantId, UUID paymentId);
}
