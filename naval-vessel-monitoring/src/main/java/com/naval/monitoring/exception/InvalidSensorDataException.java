package com.naval.monitoring.exception;

/** Thrown when a sensor reading or sensor definition fails validation. */
public class InvalidSensorDataException extends Exception {
    public InvalidSensorDataException(String message) { super(message); }
}
