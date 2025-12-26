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
import com.ankit.jobtracker.repository.JobRepository;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        jobRepository.hardDeleteAll();
        refreshTokenRepository.deleteAll();
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
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() throws Exception {

        String payload = """
        {
          "username": "user1",
          "password": "user123"
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {

        String payload = """
        {
          "username": "user1",
          "password": "wrongpassword"
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized());
    }

    // ---------------- REFRESH ----------------

    @Test
    void refresh_shouldReturnNewAccessToken_whenRefreshTokenIsValid() throws Exception {

        String loginPayload = """
        {
          "username": "user1",
          "password": "user123"
        }
        """;

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String refreshToken = JsonPath.read(loginResponse, "$.refreshToken");

        String refreshPayload = """
        {
          "refreshToken": "%s"
        }
        """.formatted(refreshToken);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshPayload))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    // ---------------- LOGOUT-ALL ----------------

    @Test
    void logoutAll_shouldInvalidateRefreshTokens() throws Exception {

        String loginPayload = """
        {
          "username": "user1",
          "password": "user123"
        }
        """;

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String accessToken = JsonPath.read(loginResponse, "$.accessToken");
        String refreshToken = JsonPath.read(loginResponse, "$.refreshToken");

       
        mockMvc.perform(post("/auth/logout-all")
        .header("Authorization", "Bearer " + accessToken))
    .andExpect(status().isNoContent()); // ✅ 204


        String refreshPayload = """
        {
          "refreshToken": "%s"
        }
        """.formatted(refreshToken);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshPayload))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutAll_shouldReturn401_whenAnonymous() throws Exception {

        mockMvc.perform(post("/auth/logout-all"))
            .andExpect(status().isUnauthorized());
    }
}
