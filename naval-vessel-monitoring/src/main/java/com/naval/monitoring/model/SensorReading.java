package com.naval.monitoring.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A single timestamped reading captured from a sensor. */
public class SensorReading {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String sensorId;
    private final double value;
    private final LocalDateTime timestamp;

    public SensorReading(String sensorId, double value, LocalDateTime timestamp) {
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getSensorId() { return sensorId; }
    public double getValue() { return value; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Reading[sensor=%s, value=%.2f, at=%s]", sensorId, value, timestamp.format(FMT));
    }

    public String toCsv() {
        return String.join(",", sensorId, String.valueOf(value), timestamp.format(FMT));
    }

    public static SensorReading fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new SensorReading(p[0], Double.parseDouble(p[1]), LocalDateTime.parse(p[2], FMT));
    }
}
