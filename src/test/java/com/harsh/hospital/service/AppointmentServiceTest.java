package com.harsh.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.harsh.hospital.domain.Appointment;
import com.harsh.hospital.domain.AppointmentStatus;
import com.harsh.hospital.domain.Doctor;
import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.domain.Role;
import com.harsh.hospital.domain.User;
import com.harsh.hospital.dto.AppointmentDtos.AppointmentRequest;
import com.harsh.hospital.dto.AppointmentDtos.CancelRequest;
import com.harsh.hospital.exception.BusinessRuleException;
import com.harsh.hospital.exception.ConflictException;
import com.harsh.hospital.mapper.AppointmentMapper;
import com.harsh.hospital.repository.AppointmentRepository;
import com.harsh.hospital.repository.DoctorRepository;
import com.harsh.hospital.repository.PatientRepository;
import com.harsh.hospital.security.AppUserDetails;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for the appointment business rules (no Spring context). */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private AppointmentMapper appointmentMapper;

    private AppointmentService service;

    private static final Long PATIENT_ID = 1L;
    private static final Long DOCTOR_ID = 5L;

    @BeforeEach
    void setUp() {
        service = new AppointmentService(
                appointmentRepository, patientRepository, doctorRepository, appointmentMapper);
    }

    private AppUserDetails patientPrincipal() {
        Patient patient = Patient.builder().id(PATIENT_ID).fullName("Alice").build();
        User user = User.builder()
                .id(10L)
                .username("alice")
                .password("x")
                .role(Role.PATIENT)
                .enabled(true)
                .patient(patient)
                .build();
        return new AppUserDetails(user);
    }

    @Test
    void book_savesScheduledAppointment_whenSlotFree() {
        LocalDateTime when = LocalDateTime.now().plusDays(1);
        when(patientRepository.findById(PATIENT_ID))
                .thenReturn(Optional.of(Patient.builder().id(PATIENT_ID).build()));
        when(doctorRepository.findById(DOCTOR_ID))
                .thenReturn(Optional.of(Doctor.builder().id(DOCTOR_ID).build()));
        when(appointmentRepository.existsByDoctorIdAndStartTimeAndStatusNot(
                        DOCTOR_ID, when, AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.book(new AppointmentRequest(null, DOCTOR_ID, when, "checkup"), patientPrincipal());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());
        Appointment saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(saved.getPatient().getId()).isEqualTo(PATIENT_ID);
        assertThat(saved.getDoctor().getId()).isEqualTo(DOCTOR_ID);
    }

    @Test
    void book_throwsConflict_whenDoctorSlotTaken() {
        LocalDateTime when = LocalDateTime.now().plusDays(1);
        when(patientRepository.findById(PATIENT_ID))
                .thenReturn(Optional.of(Patient.builder().id(PATIENT_ID).build()));
        when(doctorRepository.findById(DOCTOR_ID))
                .thenReturn(Optional.of(Doctor.builder().id(DOCTOR_ID).build()));
        when(appointmentRepository.existsByDoctorIdAndStartTimeAndStatusNot(
                        DOCTOR_ID, when, AppointmentStatus.CANCELLED))
                .thenReturn(true);

        assertThatThrownBy(() ->
                        service.book(
                                new AppointmentRequest(null, DOCTOR_ID, when, "checkup"),
                                patientPrincipal()))
                .isInstanceOf(ConflictException.class);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void book_throwsBusinessRule_whenTimeInPast() {
        LocalDateTime past = LocalDateTime.now().minusHours(1);

        assertThatThrownBy(() ->
                        service.book(
                                new AppointmentRequest(null, DOCTOR_ID, past, "checkup"),
                                patientPrincipal()))
                .isInstanceOf(BusinessRuleException.class);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancel_marksCancelledAndStampsAudit() {
        Appointment appt = Appointment.builder()
                .id(99L)
                .patient(Patient.builder().id(PATIENT_ID).build())
                .doctor(Doctor.builder().id(DOCTOR_ID).build())
                .startTime(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.SCHEDULED)
                .build();
        when(appointmentRepository.findById(99L)).thenReturn(Optional.of(appt));

        service.cancel(99L, new CancelRequest("patient unavailable"), patientPrincipal());

        assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appt.getCancelledAt()).isNotNull();
        assertThat(appt.getCancelReason()).isEqualTo("patient unavailable");
    }

    @Test
    void cancel_throwsBusinessRule_whenAlreadyCancelled() {
        Appointment appt = Appointment.builder()
                .id(99L)
                .patient(Patient.builder().id(PATIENT_ID).build())
                .doctor(Doctor.builder().id(DOCTOR_ID).build())
                .status(AppointmentStatus.CANCELLED)
                .build();
        when(appointmentRepository.findById(99L)).thenReturn(Optional.of(appt));

        assertThatThrownBy(() ->
                        service.cancel(99L, new CancelRequest("x"), patientPrincipal()))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void list_forPatient_usesPatientScopedQuery() {
        when(appointmentRepository.findByPatientId(eq(PATIENT_ID), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        service.list(patientPrincipal(), org.springframework.data.domain.PageRequest.of(0, 20));

        verify(appointmentRepository).findByPatientId(eq(PATIENT_ID), any());
        verify(appointmentRepository, never()).findAll(
                (org.springframework.data.domain.Pageable) any());
    }
}
