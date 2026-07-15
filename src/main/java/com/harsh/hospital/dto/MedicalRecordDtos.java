package com.harsh.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** Medical-record request/response DTOs. */
public final class MedicalRecordDtos {

    private MedicalRecordDtos() {}

    public record MedicalRecordRequest(
            @NotNull Long patientId,
            @NotBlank String diagnosis,
            @Size(max = 2000) String treatment,
            @Size(max = 2000) String notes) {}

    public record MedicalRecordResponse(
            Long id,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            String diagnosis,
            String treatment,
            String notes,
            Instant createdAt) {}
}
