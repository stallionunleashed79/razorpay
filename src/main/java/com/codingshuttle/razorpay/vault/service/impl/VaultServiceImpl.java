package com.codingshuttle.razorpay.vault.service.impl;

import com.codingshuttle.razorpay.vault.dto.TokenizeRequest;
import com.codingshuttle.razorpay.vault.dto.TokenizeResponse;
import com.codingshuttle.razorpay.vault.repository.CardTokenRepository;
import com.codingshuttle.razorpay.vault.repository.VaultCardRepository;
import com.codingshuttle.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;

    @Override
    public @Nullable TokenizeResponse tokenize(TokenizeRequest tokenizeRequest) {
        return null;
    }
}
