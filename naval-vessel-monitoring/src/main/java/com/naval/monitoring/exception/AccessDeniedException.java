package com.naval.monitoring.exception;

/** Thrown when an operation is attempted by an officer rank below the required clearance. */
public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message) { super(message); }
}
