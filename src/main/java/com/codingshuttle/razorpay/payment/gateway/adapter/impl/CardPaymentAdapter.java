package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentAdapter extends PaymentAdapter {
    public CardPaymentAdapter(PaymentProcessorRouter paymentProcessorRouter) {
        super(paymentProcessorRouter);
    }

    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    protected PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

}
