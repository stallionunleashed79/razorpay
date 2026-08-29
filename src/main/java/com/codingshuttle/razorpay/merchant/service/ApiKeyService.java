package com.codingshuttle.razorpay.merchant.service;

import com.codingshuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);
    List<ApiKeyResponse> listByMerchantId(UUID merchantId);
    void revoke(final UUID merchantId, final UUID apiKeyId);
    ApiKeyCreateResponse rotate(final UUID merchantId, final UUID apiKeyId);
}
