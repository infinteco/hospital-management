package com.harsh.hospital.service;

import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.domain.Role;
import com.harsh.hospital.domain.User;
import com.harsh.hospital.dto.AuthDtos.AuthResponse;
import com.harsh.hospital.dto.AuthDtos.LoginRequest;
import com.harsh.hospital.dto.AuthDtos.RegisterRequest;
import com.harsh.hospital.exception.ConflictException;
import com.harsh.hospital.repository.PatientRepository;
import com.harsh.hospital.repository.UserRepository;
import com.harsh.hospital.security.AppUserDetails;
import com.harsh.hospital.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Authentication and patient self-registration. */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Authenticate and issue a JWT. Throws {@code BadCredentialsException} on failure. */
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        AppUserDetails principal = (AppUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(principal);
        return AuthResponse.bearer(token, principal.getUsername(), principal.getRole().name());
    }

    /** Register a new PATIENT account (with its patient profile) and issue a JWT. */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken: " + request.username());
        }
        Patient patient = patientRepository.save(Patient.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .build());

        User user = userRepository.save(User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.PATIENT)
                .enabled(true)
                .patient(patient)
                .build());

        String token = jwtService.generateToken(new AppUserDetails(user));
        return AuthResponse.bearer(token, user.getUsername(), user.getRole().name());
    }
}
