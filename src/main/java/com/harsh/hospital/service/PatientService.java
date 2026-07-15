package com.harsh.hospital.service;

import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.dto.PatientDtos.PatientRequest;
import com.harsh.hospital.dto.PatientDtos.PatientResponse;
import com.harsh.hospital.exception.NotFoundException;
import com.harsh.hospital.mapper.PatientMapper;
import com.harsh.hospital.repository.PatientRepository;
import com.harsh.hospital.security.AppUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Patient CRUD with per-record ownership checks. */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient saved = patientRepository.save(patientMapper.toEntity(request));
        return patientMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> list(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    /** A patient may read only their own profile; ADMIN/DOCTOR may read any. */
    @Transactional(readOnly = true)
    public PatientResponse get(Long id, AppUserDetails caller) {
        AccessGuard.requirePatientAccess(caller, id);
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(() -> NotFoundException.of("Patient", id));
        return patientMapper.toResponse(patient);
    }
}
