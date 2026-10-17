package com.codingshuttle.razorpay.vault.dto;

import com.codingshuttle.razorpay.common.enums.CardBrand;
import lombok.Builder;

@Builder
public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand cardBrand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
