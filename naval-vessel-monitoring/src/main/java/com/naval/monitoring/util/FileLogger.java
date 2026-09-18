package com.naval.monitoring.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Minimal append-only file logger used across the system for monitoring
 * and audit purposes (non-functional requirement: logging/monitoring).
 * Uses try-with-resources so file handles are always released
 * (non-functional requirement: resource efficiency).
 */
public final class FileLogger {
    private static final String LOG_FILE = "data/system.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FileLogger() { }

    public static synchronized void log(String level, String message) {
        String line = String.format("[%s] %-5s %s", LocalDateTime.now().format(FMT), level, message);
        System.out.println(line);
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(line);
        } catch (IOException e) {
            System.err.println("WARN: could not write to log file: " + e.getMessage());
        }
    }

    public static void info(String message) { log("INFO", message); }
    public static void warn(String message) { log("WARN", message); }
    public static void error(String message) { log("ERROR", message); }
}
