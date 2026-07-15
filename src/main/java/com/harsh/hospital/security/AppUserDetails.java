package com.harsh.hospital.security;

import com.harsh.hospital.domain.Role;
import com.harsh.hospital.domain.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Authenticated principal. Carries the linked {@code patientId}/{@code doctorId}
 * so ownership checks ("can this patient read that record?") need no extra query.
 */
public class AppUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Role role;
    private final boolean enabled;
    private final Long patientId;
    private final Long doctorId;

    public AppUserDetails(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.patientId = user.getPatient() != null ? user.getPatient().getId() : null;
        this.doctorId = user.getDoctor() != null ? user.getDoctor().getId() : null;
    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
