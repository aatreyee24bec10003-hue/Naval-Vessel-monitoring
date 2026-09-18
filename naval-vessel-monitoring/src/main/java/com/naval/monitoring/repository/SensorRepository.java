package com.naval.monitoring.repository;

import com.naval.monitoring.model.Sensor;
import com.naval.monitoring.model.SensorReading;
import com.naval.monitoring.util.DataDirectory;
import java.io.*;
import java.util.*;

/** Repository for sensors and their readings, persisted as CSV files. */
public class SensorRepository {
    private static final String SENSORS_FILE = "data/sensors.csv";
    private static final String READINGS_FILE = "data/readings.csv";

    private final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private final List<SensorReading> readings = new ArrayList<>();

    public SensorRepository() {
        DataDirectory.ensureExists();
        loadSensors();
        loadReadings();
    }

    public void saveSensor(Sensor sensor) {
        sensors.put(sensor.getSensorId(), sensor);
        persistSensors();
    }

    public Optional<Sensor> findById(String sensorId) {
        return Optional.ofNullable(sensors.get(sensorId));
    }

    public List<Sensor> findByVessel(String vesselId) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : sensors.values()) {
            if (s.getVesselId().equals(vesselId)) result.add(s);
        }
        return result;
    }

    public List<Sensor> findAll() { return new ArrayList<>(sensors.values()); }

    public void addReading(SensorReading reading) {
        readings.add(reading);
        try (PrintWriter out = new PrintWriter(new FileWriter(READINGS_FILE, true))) {
            out.println(reading.toCsv());
        } catch (IOException e) {
            System.err.println("Could not persist reading: " + e.getMessage());
        }
    }

    public List<SensorReading> findReadingsBySensor(String sensorId) {
        List<SensorReading> result = new ArrayList<>();
        for (SensorReading r : readings) {
            if (r.getSensorId().equals(sensorId)) result.add(r);
        }
        return result;
    }

    private void persistSensors() {
        try (PrintWriter out = new PrintWriter(new FileWriter(SENSORS_FILE))) {
            for (Sensor s : sensors.values()) out.println(s.toCsv());
        } catch (IOException e) {
            System.err.println("Could not persist sensors: " + e.getMessage());
        }
    }

    private void loadSensors() {
        File f = new File(SENSORS_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Sensor s = Sensor.fromCsv(line);
                sensors.put(s.getSensorId(), s);
            }
        } catch (IOException e) {
            System.err.println("Could not load sensors: " + e.getMessage());
        }
    }

    private void loadReadings() {
        File f = new File(READINGS_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                readings.add(SensorReading.fromCsv(line));
            }
        } catch (IOException e) {
            System.err.println("Could not load readings: " + e.getMessage());
        }
    }
}
