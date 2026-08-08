package com.codingshuttle.razorpay.merchant.service;

import com.codingshuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);
    List<ApiKeyResponse> getByMerchantId(UUID merchantId);

}
