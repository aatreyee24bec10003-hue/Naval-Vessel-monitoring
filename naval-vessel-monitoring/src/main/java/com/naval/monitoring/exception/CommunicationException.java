package com.naval.monitoring.exception;

/** Thrown when a communication message cannot be sent, encrypted or decrypted. */
public class CommunicationException extends Exception {
    public CommunicationException(String message) { super(message); }
}
