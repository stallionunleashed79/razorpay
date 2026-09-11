package com.codingshuttle.razorpay.payment.config;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.CardPaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.NetBankingAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.impl.UPIPaymentAdapter;
import com.codingshuttle.razorpay.payment.processor.PaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentConfiguration {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NETBANKING, new NetBankingAdapter(),
                PaymentMethod.UPI, new UPIPaymentAdapter()
        );
    }

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}
