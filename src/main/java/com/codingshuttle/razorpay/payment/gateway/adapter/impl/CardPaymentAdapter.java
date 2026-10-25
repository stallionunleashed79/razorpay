package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codingshuttle.razorpay.vault.service.VaultService;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentAdapter extends PaymentAdapter {

    private final VaultService vaultService;

    // Explicit constructor handles both parent and local dependencies
    public CardPaymentAdapter(PaymentProcessorRouter paymentProcessorRouter, VaultService vaultService) {
        super(paymentProcessorRouter);
        this.vaultService = vaultService;
    }

    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        final String token = (String) paymentRequest.methodDetails().get("token");
        final PaymentProcessorResponse response = vaultService.charge(paymentRequest.paymentId(), token, paymentRequest.amount(),
                paymentRequest.methodDetails());
        return getPaymentResult(response);
    }

    @Override
    protected PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

}
