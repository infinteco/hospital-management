package com.harsh.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/** Patient request/response DTOs. */
public final class PatientDtos {

    private PatientDtos() {}

    public record PatientRequest(
            @NotBlank String fullName,
            @Email String email,
            String phone,
            @Past LocalDate dateOfBirth,
            String gender) {}

    public record PatientResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            LocalDate dateOfBirth,
            String gender) {}
}
