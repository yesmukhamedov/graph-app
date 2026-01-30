package com.example.graph.service.phone;

public final class PhoneFormatUtils {
    private PhoneFormatUtils() {
    }

    public static String formatPhone(String patternMask, String digits) {
        if (patternMask == null || digits == null) {
            return null;
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : patternMask.toCharArray()) {
            if (c == '_') {
                sb.append(i < digits.length() ? digits.charAt(i++) : '_');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
