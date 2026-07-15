package com.harsh.hospital.exception;

/** Thrown when an authenticated user accesses a resource they do not own (403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
