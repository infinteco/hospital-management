package com.harsh.hospital.exception;

/** Thrown when input violates a business rule (mapped to 422). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
