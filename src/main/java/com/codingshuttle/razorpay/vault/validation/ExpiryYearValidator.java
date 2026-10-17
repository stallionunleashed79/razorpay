package com.codingshuttle.razorpay.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            int year = Integer.parseInt(value);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return year >= currentYear && year <= 2100; // Example range for valid expiry years
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
