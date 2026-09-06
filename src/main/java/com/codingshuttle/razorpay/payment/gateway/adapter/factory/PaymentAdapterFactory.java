package com.codingshuttle.razorpay.payment.gateway.adapter.factory;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentAdapterFactory {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public PaymentAdapter getAdapter(PaymentMethod method) {
        final PaymentAdapter adapter = paymentAdapterMap.get(method);
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter found for method: " + method);
        }
        return adapter;
    }
}

