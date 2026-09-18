package com.naval.monitoring.util;

/**
 * Lightweight XOR-based cipher used to demonstrate the security
 * non-functional requirement: message bodies are never stored or
 * transmitted in plain text within the communication module.
 * (Educational demonstration only - not cryptographically strong.)
 */
public final class SimpleCipher {
    private static final int KEY = 0x5A;

    private SimpleCipher() { }

    public static String encrypt(String plainText) {
        StringBuilder sb = new StringBuilder();
        for (char c : plainText.toCharArray()) {
            sb.append(String.format("%02X", (c ^ KEY)));
        }
        return sb.toString();
    }

    public static String decrypt(String cipherHex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cipherHex.length(); i += 2) {
            int code = Integer.parseInt(cipherHex.substring(i, i + 2), 16);
            sb.append((char) (code ^ KEY));
        }
        return sb.toString();
    }
}
