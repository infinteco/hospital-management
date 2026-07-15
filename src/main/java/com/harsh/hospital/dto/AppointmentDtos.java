package com.harsh.hospital.dto;

import com.harsh.hospital.domain.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;

/** Appointment request/response DTOs. */
public final class AppointmentDtos {

    private AppointmentDtos() {}

    /**
     * Booking request. {@code patientId} is required only when an ADMIN books on
     * behalf of a patient; a PATIENT booking for themselves may omit it.
     */
    public record AppointmentRequest(
            Long patientId,
            @NotNull Long doctorId,
            @NotNull @Future LocalDateTime startTime,
            @Size(max = 500) String reason) {}

    public record CancelRequest(@Size(max = 500) String reason) {}

    public record AppointmentResponse(
            Long id,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            LocalDateTime startTime,
            AppointmentStatus status,
            String reason,
            Instant createdAt,
            Instant cancelledAt,
            String cancelReason) {}
}
