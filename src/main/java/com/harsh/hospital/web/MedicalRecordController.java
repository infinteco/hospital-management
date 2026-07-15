package com.harsh.hospital.web;

import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordRequest;
import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordResponse;
import com.harsh.hospital.security.AppUserDetails;
import com.harsh.hospital.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('DOCTOR')")
    public MedicalRecordResponse create(
            @Valid @RequestBody MedicalRecordRequest request,
            @AuthenticationPrincipal AppUserDetails caller) {
        return medicalRecordService.create(request, caller);
    }
}
