package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.LoginRequest;
import com.ankit.jobtracker.dto.LoginResponse;
import com.ankit.jobtracker.dto.RefreshTokenRequest;
import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.repository.UserRepository;
import com.ankit.jobtracker.security.JwtUtil;
import com.ankit.jobtracker.security.RefreshTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        // 1️⃣ Authenticate (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2️⃣ Load YOUR User entity from DB
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Generate access token from DB User
        String accessToken = jwtUtil.generateToken(user);

        // 4️⃣ Generate refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getUsername(),
                List.copyOf(user.getRoles())
        );
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshTokenRequest request) {

        String accessToken =
                refreshTokenService.refreshAccessToken(request.getRefreshToken());

        return new LoginResponse(
                accessToken,
                request.getRefreshToken(),
                "Bearer",
                null,
                null
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        refreshTokenService.deleteAllTokensForUser(username);

        return ResponseEntity.noContent().build();
    }



}
