package com.example.graph.validate;

import com.example.graph.model.phone.PhonePatternEntity;
import org.springframework.stereotype.Component;

@Component
public class PhoneDigitsValidator {
    public void validateDigitsAgainstPattern(String digits, PhonePatternEntity pattern) {
        if (digits == null || digits.isBlank()) {
            throw new ValidationException("Digits are required.");
        }
        if (!digits.matches("^[0-9]+$")) {
            throw new ValidationException("Digits must contain only numbers.");
        }
        long expected = pattern.getValue().chars().filter(ch -> ch == '_').count();
        if (digits.length() != expected) {
            throw new ValidationException("Phone digits length must be " + expected + " for pattern "
                + pattern.getCode() + ".");
        }
    }
}
