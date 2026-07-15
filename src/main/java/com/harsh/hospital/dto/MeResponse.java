package com.harsh.hospital.dto;

/** The authenticated user's identity, used by the frontend to scope requests. */
public record MeResponse(String username, String role, Long patientId, Long doctorId) {}
