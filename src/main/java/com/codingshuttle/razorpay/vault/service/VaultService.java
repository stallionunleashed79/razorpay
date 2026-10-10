package com.codingshuttle.razorpay.vault.service;

import com.codingshuttle.razorpay.vault.dto.TokenizeRequest;
import com.codingshuttle.razorpay.vault.dto.TokenizeResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

public interface VaultService {
    @Nullable TokenizeResponse tokenize(@Valid TokenizeRequest tokenizeRequest);
}

