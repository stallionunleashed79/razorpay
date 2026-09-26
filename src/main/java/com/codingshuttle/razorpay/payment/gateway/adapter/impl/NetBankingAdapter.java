package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NetBankingAdapter extends PaymentAdapter {
    public NetBankingAdapter(PaymentProcessorRouter paymentProcessorRouter) {
        super(paymentProcessorRouter);
    }

    @Override
    protected PaymentMethod getPaymentMethod() {
        return PaymentMethod.NETBANKING;
    }
}
