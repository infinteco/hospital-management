package com.harsh.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Authentication request/response DTOs. */
public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {}

    /** Patient self-registration. */
    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank String fullName,
            @Email String email,
            String phone,
            @Past LocalDate dateOfBirth,
            String gender) {}

    public record AuthResponse(String token, String tokenType, String username, String role) {
        public static AuthResponse bearer(String token, String username, String role) {
            return new AuthResponse(token, "Bearer", username, role);
        }
    }
}
