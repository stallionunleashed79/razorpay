package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class NetBankingAdapter implements PaymentAdapter {
    @Override
    public void initiatePayment(PaymentRequest paymentRequest) {

    }

}
