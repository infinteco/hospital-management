package com.harsh.hospital.web;

import com.harsh.hospital.dto.MeResponse;
import com.harsh.hospital.security.AppUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Returns the current authenticated user's identity. Requires authentication. */
@RestController
@RequestMapping("/api/me")
@Tag(name = "Me")
public class MeController {

    @GetMapping
    public MeResponse me(@AuthenticationPrincipal AppUserDetails user) {
        return new MeResponse(
                user.getUsername(), user.getRole().name(), user.getPatientId(), user.getDoctorId());
    }
}
