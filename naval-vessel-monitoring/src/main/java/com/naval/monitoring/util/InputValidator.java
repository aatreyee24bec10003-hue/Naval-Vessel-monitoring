package com.naval.monitoring.util;

/** Small collection of reusable validation helpers (security + reliability NFRs). */
public final class InputValidator {
    private InputValidator() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidId(String id) {
        return !isBlank(id) && id.matches("[A-Za-z0-9_-]{2,20}");
    }

    public static double parseDoubleOrDefault(String s, double def) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
