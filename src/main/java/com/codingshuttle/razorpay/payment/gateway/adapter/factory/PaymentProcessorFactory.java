package com.codingshuttle.razorpay.payment.gateway.adapter.factory;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.processor.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessor getProcessor(PaymentMethod method) {
        final PaymentProcessor adapter = paymentProcessorMap.get(method);
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter found for method: " + method);
        }
        return adapter;
    }
}

