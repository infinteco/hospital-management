package com.harsh.hospital.web;

import com.harsh.hospital.dto.DoctorDtos.DoctorRequest;
import com.harsh.hospital.dto.DoctorDtos.DoctorResponse;
import com.harsh.hospital.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "Doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponse create(@Valid @RequestBody DoctorRequest request) {
        return doctorService.create(request);
    }

    @GetMapping
    public Page<DoctorResponse> list(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return doctorService.list(pageable);
    }

    @GetMapping("/{id}")
    public DoctorResponse get(@PathVariable Long id) {
        return doctorService.get(id);
    }
}
