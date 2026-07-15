package com.harsh.hospital.domain;

/** Application roles. Stored as strings; Spring authorities are {@code ROLE_<name>}. */
public enum Role {
    ADMIN,
    DOCTOR,
    PATIENT
}
