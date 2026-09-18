package com.naval.monitoring.model;

/** A sensor unit mounted on a vessel (sonar, radar, GPS, etc.). */
public class Sensor {
    private final String sensorId;
    private final String vesselId;
    private final SensorType type;
    private double warningThreshold;

    public Sensor(String sensorId, String vesselId, SensorType type, double warningThreshold) {
        this.sensorId = sensorId;
        this.vesselId = vesselId;
        this.type = type;
        this.warningThreshold = warningThreshold;
    }

    public String getSensorId() { return sensorId; }
    public String getVesselId() { return vesselId; }
    public SensorType getType() { return type; }
    public double getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(double t) { this.warningThreshold = t; }

    @Override
    public String toString() {
        return String.format("Sensor[id=%s, vessel=%s, type=%s, threshold=%.2f]",
                sensorId, vesselId, type, warningThreshold);
    }

    public String toCsv() {
        return String.join(",", sensorId, vesselId, type.name(), String.valueOf(warningThreshold));
    }

    public static Sensor fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Sensor(p[0], p[1], SensorType.valueOf(p[2]), Double.parseDouble(p[3]));
    }
}
