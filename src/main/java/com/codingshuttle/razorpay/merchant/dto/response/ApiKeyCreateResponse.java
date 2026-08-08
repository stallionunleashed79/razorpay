package com.codingshuttle.razorpay.merchant.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        String environment
) {
}
