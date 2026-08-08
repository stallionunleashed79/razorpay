package com.codingshuttle.razorpay.merchant.dto.response;

import com.codingshuttle.razorpay.common.enums.Environment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ApiKeyResponse(
        UUID id,
        String keyId,
        UUID merchantId,
        Environment environment,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt) {
}
