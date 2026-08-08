package com.codingshuttle.razorpay.merchant.service;

import com.codingshuttle.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.codingshuttle.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface ApiKeyService {

    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);
}
