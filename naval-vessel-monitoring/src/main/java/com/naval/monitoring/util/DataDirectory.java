package com.naval.monitoring.util;

import java.io.File;

/** Ensures the data/ persistence directory exists before any repository reads or writes to it. */
public final class DataDirectory {
    private DataDirectory() { }

    public static void ensureExists() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
