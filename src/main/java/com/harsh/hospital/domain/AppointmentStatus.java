package com.harsh.hospital.domain;

/** Lifecycle of an appointment. Cancellation is a soft delete (see audit fields). */
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}
