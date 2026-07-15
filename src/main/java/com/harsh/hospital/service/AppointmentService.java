package com.harsh.hospital.service;

import com.harsh.hospital.domain.Appointment;
import com.harsh.hospital.domain.AppointmentStatus;
import com.harsh.hospital.domain.Doctor;
import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.domain.Role;
import com.harsh.hospital.dto.AppointmentDtos.AppointmentRequest;
import com.harsh.hospital.dto.AppointmentDtos.AppointmentResponse;
import com.harsh.hospital.dto.AppointmentDtos.CancelRequest;
import com.harsh.hospital.exception.BusinessRuleException;
import com.harsh.hospital.exception.ConflictException;
import com.harsh.hospital.exception.NotFoundException;
import com.harsh.hospital.mapper.AppointmentMapper;
import com.harsh.hospital.repository.AppointmentRepository;
import com.harsh.hospital.repository.DoctorRepository;
import com.harsh.hospital.repository.PatientRepository;
import com.harsh.hospital.security.AppUserDetails;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Appointment booking, cancellation and retrieval with the core business rules. */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentMapper = appointmentMapper;
    }

    /**
     * Book an appointment. A PATIENT books for themselves; an ADMIN must supply
     * {@code patientId}. Rejects past times (422) and double-booked doctor slots
     * (409). The DB unique index on {@code (doctor_id, active_slot)} is the final
     * guard against a concurrent race.
     */
    @Transactional
    public AppointmentResponse book(AppointmentRequest request, AppUserDetails caller) {
        Long patientId = resolvePatientId(request, caller);

        if (!request.startTime().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Appointment time must be in the future.");
        }

        Patient patient = patientRepository
                .findById(patientId)
                .orElseThrow(() -> NotFoundException.of("Patient", patientId));
        Doctor doctor = doctorRepository
                .findById(request.doctorId())
                .orElseThrow(() -> NotFoundException.of("Doctor", request.doctorId()));

        if (appointmentRepository.existsByDoctorIdAndStartTimeAndStatusNot(
                doctor.getId(), request.startTime(), AppointmentStatus.CANCELLED)) {
            throw new ConflictException(
                    "Doctor already has an appointment at " + request.startTime() + ".");
        }

        Appointment saved = appointmentRepository.save(Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .startTime(request.startTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.reason())
                .build());
        return appointmentMapper.toResponse(saved);
    }

    /** Cancel an appointment (soft delete: status CANCELLED + audit trail). */
    @Transactional
    public AppointmentResponse cancel(Long id, CancelRequest request, AppUserDetails caller) {
        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> NotFoundException.of("Appointment", id));
        AccessGuard.requireAppointmentAccess(
                caller, appointment.getPatient().getId(), appointment.getDoctor().getId());

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Appointment is already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(Instant.now());
        appointment.setCancelReason(request != null ? request.reason() : null);
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id, AppUserDetails caller) {
        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> NotFoundException.of("Appointment", id));
        AccessGuard.requireAppointmentAccess(
                caller, appointment.getPatient().getId(), appointment.getDoctor().getId());
        return appointmentMapper.toResponse(appointment);
    }

    /** List appointments scoped to the caller's role. */
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> list(AppUserDetails caller, Pageable pageable) {
        Page<Appointment> page;
        if (caller.getRole() == Role.ADMIN) {
            page = appointmentRepository.findAll(pageable);
        } else if (caller.getRole() == Role.DOCTOR) {
            page = appointmentRepository.findByDoctorId(caller.getDoctorId(), pageable);
        } else {
            page = appointmentRepository.findByPatientId(caller.getPatientId(), pageable);
        }
        return page.map(appointmentMapper::toResponse);
    }

    private Long resolvePatientId(AppointmentRequest request, AppUserDetails caller) {
        if (caller.getRole() == Role.PATIENT) {
            return caller.getPatientId();
        }
        if (request.patientId() == null) {
            throw new BusinessRuleException("patientId is required when booking on behalf of a patient.");
        }
        return request.patientId();
    }
}
