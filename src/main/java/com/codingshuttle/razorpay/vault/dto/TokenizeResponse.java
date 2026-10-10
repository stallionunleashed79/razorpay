package com.codingshuttle.razorpay.vault.dto;

import com.codingshuttle.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand cardBrand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
