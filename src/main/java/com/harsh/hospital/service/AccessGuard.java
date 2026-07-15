package com.harsh.hospital.service;

import com.harsh.hospital.exception.ForbiddenException;
import com.harsh.hospital.security.AppUserDetails;

/** Ownership checks shared across services. */
final class AccessGuard {

    private AccessGuard() {}

    /**
     * Staff (ADMIN, DOCTOR) may access any patient's data; a PATIENT may access
     * only their own. Throws {@link ForbiddenException} otherwise.
     */
    static void requirePatientAccess(AppUserDetails caller, Long patientId) {
        switch (caller.getRole()) {
            case ADMIN, DOCTOR -> {
                /* staff may access any patient */
            }
            case PATIENT -> {
                if (patientId == null || !patientId.equals(caller.getPatientId())) {
                    throw new ForbiddenException("You can only access your own records.");
                }
            }
        }
    }

    /**
     * ADMIN may access any appointment; a DOCTOR only appointments assigned to
     * them; a PATIENT only their own. Throws {@link ForbiddenException} otherwise.
     */
    static void requireAppointmentAccess(AppUserDetails caller, Long patientId, Long doctorId) {
        switch (caller.getRole()) {
            case ADMIN -> {
                /* admin may access any appointment */
            }
            case DOCTOR -> {
                if (doctorId == null || !doctorId.equals(caller.getDoctorId())) {
                    throw new ForbiddenException("You can only access your own appointments.");
                }
            }
            case PATIENT -> {
                if (patientId == null || !patientId.equals(caller.getPatientId())) {
                    throw new ForbiddenException("You can only access your own appointments.");
                }
            }
        }
    }
}
