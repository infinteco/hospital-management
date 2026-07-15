package com.harsh.hospital.repository;

import com.harsh.hospital.domain.Appointment;
import com.harsh.hospital.domain.AppointmentStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** True if the doctor already has a non-cancelled appointment at that time. */
    boolean existsByDoctorIdAndStartTimeAndStatusNot(
            Long doctorId, LocalDateTime startTime, AppointmentStatus status);

    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
}
