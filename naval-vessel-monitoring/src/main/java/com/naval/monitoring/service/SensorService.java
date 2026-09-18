package com.naval.monitoring.service;

import com.naval.monitoring.exception.InvalidSensorDataException;
import com.naval.monitoring.exception.VesselNotFoundException;
import com.naval.monitoring.model.Sensor;
import com.naval.monitoring.model.SensorReading;
import com.naval.monitoring.model.SensorType;
import com.naval.monitoring.repository.SensorRepository;
import com.naval.monitoring.util.FileLogger;
import com.naval.monitoring.util.InputValidator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic for the Sensor Data Management module: registering
 * sensors, recording readings, and raising threshold alerts.
 */
public class SensorService {
    private final SensorRepository sensorRepository;
    private final VesselService vesselService;

    public SensorService(SensorRepository sensorRepository, VesselService vesselService) {
        this.sensorRepository = sensorRepository;
        this.vesselService = vesselService;
    }

    public Sensor registerSensor(String sensorId, String vesselId, SensorType type, double threshold)
            throws VesselNotFoundException, InvalidSensorDataException {
        if (!InputValidator.isValidId(sensorId)) {
            throw new InvalidSensorDataException("Invalid sensor id: " + sensorId);
        }
        vesselService.getVessel(vesselId); // throws VesselNotFoundException if absent
        Sensor sensor = new Sensor(sensorId, vesselId, type, threshold);
        sensorRepository.saveSensor(sensor);
        FileLogger.info("Sensor registered: " + sensor);
        return sensor;
    }

    /**
     * Records a reading for a sensor and evaluates it against the sensor's
     * warning threshold, logging an alert when the threshold is exceeded.
     */
    public SensorReading recordReading(String sensorId, double value) throws InvalidSensorDataException {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new InvalidSensorDataException("Unknown sensor id: " + sensorId));
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new InvalidSensorDataException("Reading value is not a finite number");
        }
        SensorReading reading = new SensorReading(sensorId, value, LocalDateTime.now());
        sensorRepository.addReading(reading);

        if (Math.abs(value) >= sensor.getWarningThreshold()) {
            FileLogger.warn(String.format("ALERT: %s reading %.2f exceeds threshold %.2f on vessel %s",
                    sensor.getType(), value, sensor.getWarningThreshold(), sensor.getVesselId()));
        } else {
            FileLogger.info("Reading recorded: " + reading);
        }
        return reading;
    }

    public List<Sensor> listSensorsForVessel(String vesselId) {
        return sensorRepository.findByVessel(vesselId);
    }

    public List<SensorReading> getHistory(String sensorId) {
        return sensorRepository.findReadingsBySensor(sensorId);
    }

    public List<Sensor> listAllSensors() {
        return sensorRepository.findAll();
    }
}
