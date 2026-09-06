package com.codingshuttle.razorpay.payment.gateway.adapter;

import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;

import javax.smartcardio.Card;

public interface PaymentAdapter {

    void initiatePayment(PaymentRequest paymentRequest) throws Exception;
}
