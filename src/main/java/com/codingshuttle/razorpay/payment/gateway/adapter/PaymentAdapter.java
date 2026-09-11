package com.codingshuttle.razorpay.payment.gateway.adapter;

import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;

public interface PaymentAdapter {

    void initiatePayment(PaymentRequest paymentRequest);
}
