package com.codingshuttle.razorpay.payment.dto;

import com.codingshuttle.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @Size(max = 100)
        String receipt,
        LocalDateTime expiresAt,
        Map<String, Object> notes,
        @NotNull(message = "Amount is required")
        Money amount
) {
}
