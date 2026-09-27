package com.codingshuttle.razorpay.payment.gateway.adapter.factory;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentAdapterRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public PaymentResult initiate(PaymentRequest paymentRequest) {
        final PaymentAdapter adapter = paymentAdapterMap.get(paymentRequest.paymentMethod());
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter found for paymentRequest: " + paymentRequest);
        }
        return adapter.initiatePayment(paymentRequest);
    }

    public PaymentResult capture(final PaymentMethod paymentMethod, final UUID paymentId) {
        final PaymentAdapter adapter = paymentAdapterMap.get(paymentMethod);
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter found for paymentMethod: " + paymentMethod);
        }
        return adapter.capture(paymentId);
    }
}

