package com.harsh.hospital.service;

import com.harsh.hospital.domain.Doctor;
import com.harsh.hospital.dto.DoctorDtos.DoctorRequest;
import com.harsh.hospital.dto.DoctorDtos.DoctorResponse;
import com.harsh.hospital.exception.NotFoundException;
import com.harsh.hospital.mapper.DoctorMapper;
import com.harsh.hospital.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Doctor CRUD. Doctor listings are readable by any authenticated user. */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        Doctor saved = doctorRepository.save(doctorMapper.toEntity(request));
        return doctorMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> list(Pageable pageable) {
        return doctorRepository.findAll(pageable).map(doctorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DoctorResponse get(Long id) {
        Doctor doctor = doctorRepository
                .findById(id)
                .orElseThrow(() -> NotFoundException.of("Doctor", id));
        return doctorMapper.toResponse(doctor);
    }
}
