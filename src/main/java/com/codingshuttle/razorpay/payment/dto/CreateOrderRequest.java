package com.codingshuttle.razorpay.payment.dto;

import com.codingshuttle.razorpay.common.entity.Money;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        String receipt,
        LocalDateTime expiresAt,
        Map<String, Object> notes,
        Money amount
) {
}
