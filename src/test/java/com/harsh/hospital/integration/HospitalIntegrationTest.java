package com.harsh.hospital.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Full-stack integration tests against a real MySQL (Testcontainers). Flyway
 * builds the schema + seed, and Hibernate {@code ddl-auto=validate} confirms the
 * entities match the migration.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class HospitalIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    /** Log in a seeded user and return the bearer token. */
    private String login(String username) throws Exception {
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<>() {
                    {
                        put("username", username);
                        put("password", "Password123!");
                    }
                });
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    @Test
    void seededUserCanLogIn() throws Exception {
        String token = login("alice");
        org.assertj.core.api.Assertions.assertThat(token).isNotBlank();
    }

    @Test
    void patientCanReadOwnMedicalRecords() throws Exception {
        String alice = login("alice"); // alice is patient id 1, which has a seeded record
        mockMvc.perform(get("/api/patients/1/medical-records").header("Authorization", "Bearer " + alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].diagnosis").value(containsString("hypertension")));
    }

    /** The core security guarantee: a patient cannot read another patient's records. */
    @Test
    void patientCannotReadAnotherPatientsMedicalRecords() throws Exception {
        String bob = login("bob"); // bob is patient id 2
        mockMvc.perform(get("/api/patients/1/medical-records").header("Authorization", "Bearer " + bob))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookingConflictReturns409() throws Exception {
        String alice = login("alice");
        // The seed already books doctor 1 at 2030-01-15T10:00, so this collides.
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<>() {
                    {
                        put("doctorId", 1);
                        put("startTime", "2030-01-15T10:00:00");
                        put("reason", "clash");
                    }
                });
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + alice)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void pastAppointmentIsRejected() throws Exception {
        String alice = login("alice");
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<>() {
                    {
                        put("doctorId", 1);
                        put("startTime", "2000-01-01T09:00:00");
                        put("reason", "past");
                    }
                });
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + alice)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest()); // @Future validation
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/appointments")).andExpect(status().isUnauthorized());
    }

    @Test
    void patientCannotCreateDoctor() throws Exception {
        String alice = login("alice");
        String body = objectMapper.writeValueAsString(
                new java.util.HashMap<>() {
                    {
                        put("fullName", "Dr. New");
                        put("specialization", "General");
                    }
                });
        mockMvc.perform(post("/api/doctors")
                        .header("Authorization", "Bearer " + alice)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }
}
