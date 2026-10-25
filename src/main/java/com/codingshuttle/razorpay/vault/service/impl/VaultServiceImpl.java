package com.codingshuttle.razorpay.vault.service.impl;

import com.codingshuttle.razorpay.common.entity.Money;
import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.codingshuttle.razorpay.vault.config.VaultEncryptionConfig;
import com.codingshuttle.razorpay.vault.dto.TokenizeRequest;
import com.codingshuttle.razorpay.vault.dto.TokenizeResponse;
import com.codingshuttle.razorpay.vault.entity.CardToken;
import com.codingshuttle.razorpay.vault.entity.VaultCard;
import com.codingshuttle.razorpay.vault.repository.CardTokenRepository;
import com.codingshuttle.razorpay.vault.repository.VaultCardRepository;
import com.codingshuttle.razorpay.vault.service.VaultService;
import com.codingshuttle.razorpay.vault.util.VaultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import com.codingshuttle.razorpay.common.enums.CardBrand;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final VaultEncryptionConfig vaultEncryptionConfig;
    private final BytesEncryptor dekEncryptor;

    final UUID merchantId = UUID.fromString("499dc8d8-e552-4782-a707-8b2bbf80b9b5"); //TODO - Replace with id from merchant context
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public @Nullable TokenizeResponse tokenize(final TokenizeRequest tokenizeRequest) {
        final String pan = tokenizeRequest.pan();
        final String lastFourDigits = pan.substring(pan.length() - 4);
        final String bin = pan.substring(0, 6);
        final CardBrand cardBand = resolveBrand(pan);
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultUtils.panEncryptor(dek).encrypt(pan.getBytes(
                StandardCharsets.UTF_8
        ));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);
        final VaultCard vaultCard = VaultCard.builder()
                .brand(cardBand.name())
                .lastFour(lastFourDigits)
                .bin(bin)
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .expiryMonth(tokenizeRequest.expiryMonth())
                .expiryYear(tokenizeRequest.expiryYear())
                .cardHolderName(tokenizeRequest.cardHolderName())
                .build();
        vaultCardRepository.save(vaultCard);

        final String token = "tok_" + RandomizerUtil.randomBase64(32);
        cardTokenRepository.save(CardToken.builder()
                .token(token)
                .vaultCard(vaultCard)
                .customer(tokenizeRequest.customerId())
                .merchant(merchantId)
                .build());
        return TokenizeResponse.builder()
                .cardBrand(cardBand)
                .lastFour(lastFourDigits)
                .expiryMonth(tokenizeRequest.expiryMonth())
                .expiryYear(tokenizeRequest.expiryYear())
                .token(token)
                .build();
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {
        final CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token).orElseThrow(() ->
                new ResourceNotFoundException("CardToken", token));
        final VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;
        try {
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultUtils.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());
            final String pan = new String(panBytes, StandardCharsets.UTF_8);
            final String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();
            final PaymentProcessorRequest paymentProcessorRequest =
                    PaymentProcessorRequest.card(paymentId, amount, pan, expiry,
                            methodDetails);
            final PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);
            log.info("Vault charge registered, token={}", token.substring(0, 4));
            return response;
        } catch (Exception e) {
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (panBytes != null) {
                Arrays.fill(panBytes, (byte) 0);
            }
        }
    }

    private CardBrand resolveBrand(final String pan) {
        return switch (pan) {
            case String s when s.startsWith("4") -> CardBrand.VISA;
            case String s when s.startsWith("5") || s.startsWith("2") -> CardBrand.MASTERCARD;
            case String s when s.startsWith("37") || s.startsWith("34")-> CardBrand.AMEX;
            default -> CardBrand.RUPAY;
        };
    }

}
