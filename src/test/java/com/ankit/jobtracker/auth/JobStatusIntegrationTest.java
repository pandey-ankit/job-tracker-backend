package com.ankit.jobtracker.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.repository.UserRepository;
import com.ankit.jobtracker.repository.JobRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobStatusIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String user1Token;
    private String user2Token;
    private Long jobId;

    @BeforeEach
    void setup() throws Exception {

    jobRepository.hardDeleteAll();
    userRepository.deleteAll();

    User user1 = new User();
    user1.setUsername("user1");
    user1.setPassword(passwordEncoder.encode("user123"));
    user1.setRoles(Set.of("ROLE_USER"));
    userRepository.save(user1);

    User user2 = new User();
    user2.setUsername("user2");
    user2.setPassword(passwordEncoder.encode("user123"));
    user2.setRoles(Set.of("ROLE_USER"));
    userRepository.save(user2);

    user1Token = loginAndGetToken("user1", "user123");
    user2Token = loginAndGetToken("user2", "user123");

    String createJobPayload = """
    {
      "title": "Backend Engineer",
      "description": "Spring Boot Job",
      "location": "Remote"
    }
    """;

    String jobResponse = mockMvc.perform(post("/jobs")
                    .header("Authorization", "Bearer " + user1Token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJobPayload))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    jobId = ((Number) JsonPath.read(jobResponse, "$.id")).longValue();
}


    @Test
    void shouldAllowValidStatusTransition() throws Exception {

        String payload = """
        {
          "status": "APPLIED"
        }
        """;

        mockMvc.perform(patch("/jobs/{id}/status", jobId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void shouldRejectInvalidStatusTransition() throws Exception {

        String payload = """
        {
          "status": "ACCEPTED"
        }
        """;

        mockMvc.perform(patch("/jobs/{id}/status", jobId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Invalid job status transition")));
    }

    @Test
    void shouldReturn403_whenUpdatingOtherUsersJob() throws Exception {

        String payload = """
        {
          "status": "APPLIED"
        }
        """;

        mockMvc.perform(patch("/jobs/{id}/status", jobId)
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    /* ---------------- HELPER ---------------- */

    private String loginAndGetToken(String username, String password) throws Exception {

        String payload = """
        {
          "username": "%s",
          "password": "%s"
        }
        """.formatted(username, password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.accessToken");
    }
}