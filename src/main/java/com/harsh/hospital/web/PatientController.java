package com.harsh.hospital.web;

import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordResponse;
import com.harsh.hospital.dto.PatientDtos.PatientRequest;
import com.harsh.hospital.dto.PatientDtos.PatientResponse;
import com.harsh.hospital.security.AppUserDetails;
import com.harsh.hospital.service.MedicalRecordService;
import com.harsh.hospital.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients")
public class PatientController {

    private final PatientService patientService;
    private final MedicalRecordService medicalRecordService;

    public PatientController(
            PatientService patientService, MedicalRecordService medicalRecordService) {
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patientService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PatientResponse> list(
            @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return patientService.list(pageable);
    }

    @GetMapping("/{id}")
    public PatientResponse get(
            @PathVariable Long id, @AuthenticationPrincipal AppUserDetails caller) {
        return patientService.get(id, caller);
    }

    /** A patient may read only their own records; enforced in the service. */
    @GetMapping("/{id}/medical-records")
    public Page<MedicalRecordResponse> medicalRecords(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUserDetails caller,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return medicalRecordService.listForPatient(id, caller, pageable);
    }
}
