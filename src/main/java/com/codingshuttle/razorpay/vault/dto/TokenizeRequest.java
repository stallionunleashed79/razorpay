package com.codingshuttle.razorpay.vault.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(

        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "PAN is invalid")
        @Pattern(regexp = "^[0-9]{12,19}$", message = "PAN must be between 12 and 19 digits")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
        String cvv,

        @NotBlank(message = "Expiry month is required")
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be between 01 and 12")
        @Min(value = 1, message = "Expiry month must be between 01 and 12")
        @Max(value = 12, message = "Expiry month must be between 01 and 12")
        Integer expiryMonth,

        @NotBlank(message = "Expiry year is required")
        @Pattern(regexp = "^[0-9]{4}$", message = "Expiry year must be 4 digits")
        @Min(value = 1, message = "Expiry year must be between 01 and 12")
        @Max(value = 12, message = "Expiry year must be between 01 and 12")
        Integer expiryYear,
        UUID customerId
) {
}
