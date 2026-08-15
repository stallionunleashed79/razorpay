package com.codingshuttle.razorpay.payment.dto.response;

import com.codingshuttle.razorpay.common.entity.Money;
import com.codingshuttle.razorpay.common.enums.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus orderStatus,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
