package com.naval.monitoring.exception;

/** Thrown when an operation references a vessel id that is not registered. */
public class VesselNotFoundException extends Exception {
    public VesselNotFoundException(String vesselId) {
        super("Vessel not found: " + vesselId);
    }
}
