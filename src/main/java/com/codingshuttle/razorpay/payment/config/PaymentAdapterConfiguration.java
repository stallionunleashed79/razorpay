package com.codingshuttle.razorpay.payment.config;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.CardPaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.NetBankingAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.UPIPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfiguration {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UPIPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }

}
