package com.harsh.hospital.repository;

import com.harsh.hospital.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    /** Eagerly loads the linked patient/doctor so authentication needs no open session. */
    @EntityGraph(attributePaths = {"patient", "doctor"})
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
