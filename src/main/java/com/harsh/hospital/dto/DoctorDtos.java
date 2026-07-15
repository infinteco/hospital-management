package com.harsh.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Doctor request/response DTOs. */
public final class DoctorDtos {

    private DoctorDtos() {}

    public record DoctorRequest(
            @NotBlank String fullName,
            @NotBlank String specialization,
            @Email String email,
            String phone) {}

    public record DoctorResponse(
            Long id, String fullName, String specialization, String email, String phone) {}
}
