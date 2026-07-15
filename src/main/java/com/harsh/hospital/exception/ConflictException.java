package com.harsh.hospital.exception;

/** Thrown on a business conflict such as double-booking a slot (mapped to 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
