package com.codingshuttle.razorpay.merchant.service.impl;

import com.codingshuttle.razorpay.common.exception.ResourceNotFoundException;
import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.codingshuttle.razorpay.merchant.dto.response.MerchantResponse;
import com.codingshuttle.razorpay.merchant.entity.ApiKey;
import com.codingshuttle.razorpay.merchant.entity.Merchant;
import com.codingshuttle.razorpay.merchant.repository.ApiKeyRepository;
import com.codingshuttle.razorpay.merchant.repository.MerchantRepository;
import com.codingshuttle.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .keyId(apiKey.getKeyId())
                .keySecret(apiKey.getKeySecretHash())
                .environment(apiKey.getEnvironment().name().toLowerCase(Locale.ROOT))
                .build();
    }
}
