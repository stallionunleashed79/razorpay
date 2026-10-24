package com.codingshuttle.razorpay.vault.service;

import com.codingshuttle.razorpay.common.entity.Money;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codingshuttle.razorpay.vault.dto.TokenizeRequest;
import com.codingshuttle.razorpay.vault.dto.TokenizeResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    @Nullable TokenizeResponse tokenize(@Valid TokenizeRequest tokenizeRequest);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> stringObjectMap);
}

