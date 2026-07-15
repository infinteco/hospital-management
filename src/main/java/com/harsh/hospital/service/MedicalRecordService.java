package com.harsh.hospital.service;

import com.harsh.hospital.domain.Doctor;
import com.harsh.hospital.domain.MedicalRecord;
import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordRequest;
import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordResponse;
import com.harsh.hospital.exception.BusinessRuleException;
import com.harsh.hospital.exception.NotFoundException;
import com.harsh.hospital.mapper.MedicalRecordMapper;
import com.harsh.hospital.repository.DoctorRepository;
import com.harsh.hospital.repository.MedicalRecordRepository;
import com.harsh.hospital.repository.PatientRepository;
import com.harsh.hospital.security.AppUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Medical records: authored by doctors, readable only by the owning patient (or staff). */
@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordMapper recordMapper;

    public MedicalRecordService(
            MedicalRecordRepository recordRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            MedicalRecordMapper recordMapper) {
        this.recordRepository = recordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.recordMapper = recordMapper;
    }

    /** Create a record. Authored by the calling DOCTOR (enforced at the controller). */
    @Transactional
    public MedicalRecordResponse create(MedicalRecordRequest request, AppUserDetails caller) {
        if (caller.getDoctorId() == null) {
            throw new BusinessRuleException("Only a doctor account can author medical records.");
        }
        Patient patient = patientRepository
                .findById(request.patientId())
                .orElseThrow(() -> NotFoundException.of("Patient", request.patientId()));
        Doctor doctor = doctorRepository
                .findById(caller.getDoctorId())
                .orElseThrow(() -> NotFoundException.of("Doctor", caller.getDoctorId()));

        MedicalRecord saved = recordRepository.save(MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.diagnosis())
                .treatment(request.treatment())
                .notes(request.notes())
                .build());
        return recordMapper.toResponse(saved);
    }

    /**
     * List a patient's records. A PATIENT may read only their own; ADMIN/DOCTOR
     * may read any. This is the endpoint the security test proves is protected.
     */
    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> listForPatient(
            Long patientId, AppUserDetails caller, Pageable pageable) {
        AccessGuard.requirePatientAccess(caller, patientId);
        if (!patientRepository.existsById(patientId)) {
            throw NotFoundException.of("Patient", patientId);
        }
        return recordRepository.findByPatientId(patientId, pageable).map(recordMapper::toResponse);
    }
}
