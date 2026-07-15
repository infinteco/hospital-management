package com.harsh.hospital.web;

import com.harsh.hospital.dto.AppointmentDtos.AppointmentRequest;
import com.harsh.hospital.dto.AppointmentDtos.AppointmentResponse;
import com.harsh.hospital.dto.AppointmentDtos.CancelRequest;
import com.harsh.hospital.security.AppUserDetails;
import com.harsh.hospital.service.AppointmentService;
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
@RequestMapping("/api/appointments")
@Tag(name = "Appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public AppointmentResponse book(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal AppUserDetails caller) {
        return appointmentService.book(request, caller);
    }

    @GetMapping
    public Page<AppointmentResponse> list(
            @AuthenticationPrincipal AppUserDetails caller,
            @PageableDefault(size = 20, sort = "startTime") Pageable pageable) {
        return appointmentService.list(caller, pageable);
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(
            @PathVariable Long id, @AuthenticationPrincipal AppUserDetails caller) {
        return appointmentService.get(id, caller);
    }

    /** Cancel an appointment (soft delete). Ownership enforced in the service. */
    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CancelRequest request,
            @AuthenticationPrincipal AppUserDetails caller) {
        return appointmentService.cancel(id, request, caller);
    }
}
