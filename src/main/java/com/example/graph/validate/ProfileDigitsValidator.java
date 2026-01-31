package com.example.graph.validate;

import org.springframework.stereotype.Component;

@Component
public class ProfileDigitsValidator {
    private static final int MAX_LENGTH = 32;

    public void validateDigits(String digits) {
        if (digits == null || digits.isBlank()) {
            throw new ValidationException("Phone digits are required.");
        }
        if (digits.length() > MAX_LENGTH) {
            throw new ValidationException("Phone digits must be at most 32 characters.");
        }
        if (!digits.matches("^[0-9]+$")) {
            throw new ValidationException("Phone digits must contain only numbers.");
        }
    }

    public void validateDigits(String digits, String fieldPrefix, ValidationErrorCollector errors) {
        if (digits == null || digits.isBlank()) {
            errors.add(fieldPrefix, "Phone digits are required.");
            return;
        }
        if (digits.length() > MAX_LENGTH) {
            errors.add(fieldPrefix, "Phone digits must be at most 32 characters.");
            return;
        }
        if (!digits.matches("^[0-9]+$")) {
            errors.add(fieldPrefix, "Phone digits must contain only numbers.");
        }
    }
}
