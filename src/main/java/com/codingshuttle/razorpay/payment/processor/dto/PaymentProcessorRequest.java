package com.codingshuttle.razorpay.payment.processor.dto;

import com.codingshuttle.razorpay.common.entity.Money;
import com.codingshuttle.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,
        UUID paymentId,
        PaymentMethod method,
        Money amount,
        String pan,
        String expiry,
        Map<String, Object> paymentDetails
) {

    public static PaymentProcessorRequest card(UUID paymentId, Money amount, String pan, String expiry, Map<String, Object> paymentDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, PaymentMethod.CARD, amount, pan, expiry, paymentDetails);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod method, Money amount, Map<String, Object> paymentDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, method, amount, null, null, paymentDetails);
    }
}
