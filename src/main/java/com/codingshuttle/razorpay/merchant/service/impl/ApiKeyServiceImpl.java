package com.codingshuttle.razorpay.merchant.service.impl;

import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyResponse;
import com.codingshuttle.razorpay.merchant.entity.ApiKey;
import com.codingshuttle.razorpay.merchant.entity.Merchant;
import com.codingshuttle.razorpay.merchant.repository.ApiKeyRepository;
import com.codingshuttle.razorpay.merchant.repository.MerchantRepository;
import com.codingshuttle.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest) {
        final Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        final String keyId = String.format("rzp_%s_%s", createApiKeyRequest.environment().name().toUpperCase(), RandomizerUtil.randomBase64(
                24));;
        final String keySecret = RandomizerUtil.randomBase64(40);
        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(keySecret)
                .environment(createApiKeyRequest.environment())
                .merchant(merchant)
                .enabled(true)
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .keyId(apiKey.getKeyId())
                .keySecret(apiKey.getKeySecretHash())
                .environment(apiKey.getEnvironment().name().toLowerCase(Locale.ROOT))
                .build();
    }

    @Override
    public List<ApiKeyResponse> getByMerchantId(UUID merchantId) {
        final Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));
             return apiKeyRepository.findByMerchant(merchant).stream()
                .map(apiKey -> ApiKeyResponse.builder()
                        .id(apiKey.getId())
                        .keyId(apiKey.getKeyId())
                        .merchantId(apiKey.getMerchant().getId())
                        .environment(apiKey.getEnvironment())
                        .lastUsedAt(apiKey.getLastUsedAt())
                        .createdAt(apiKey.getCreatedAt())
                        .build())
                .toList();}

    @Override
    public void revoke(final UUID merchantId, final UUID apiKeyId) {
        final ApiKey apiKey = validateApiKey(merchantId, apiKeyId);
        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);
    }

    @Override
    public ApiKeyCreateResponse rotate(final UUID merchantId, final UUID apiKeyId) {
        final ApiKey apiKey = validateApiKey(merchantId, apiKeyId);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        final String keySecret = RandomizerUtil.randomBase64(40);
             apiKey.setKeySecretHash(keySecret);
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKeyRepository.save(apiKey);
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .keyId(apiKey.getKeyId())
                .keySecret(apiKey.getKeySecretHash())
                .environment(apiKey.getEnvironment().name().toLowerCase(Locale.ROOT))
                .build();
    }

    /**
     * Validate apikey with merchantId
     * @param merchantId
     * @param apiKeyId
     * @return
     */
    private ApiKey validateApiKey(UUID merchantId, UUID apiKeyId) {
        final Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));
        final ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new ResourceNotFoundException("api_key", apiKeyId));
        if (!apiKey.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("api_key", apiKeyId);
        }
        return apiKey;
    }
}
