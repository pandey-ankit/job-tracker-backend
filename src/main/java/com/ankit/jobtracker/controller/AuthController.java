package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.AuthRequestDto;
import com.ankit.jobtracker.dto.AuthResponseDto;
import com.ankit.jobtracker.dto.RefreshTokenRequestDto;
import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.security.JwtUtil;
import com.ankit.jobtracker.security.CustomUserDetailsService;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService,
                          RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        var userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtUtil.generateToken(userDetails);

        // create refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(userDetails.getUsername());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days

        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDto(accessToken, refreshToken.getToken());
    }

    // 🔁 REFRESH
    @PostMapping("/refresh")
    public AuthResponseDto refresh(@RequestBody RefreshTokenRequestDto request) {

        RefreshToken token = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        var userDetails =
                userDetailsService.loadUserByUsername(token.getUsername());

        String newAccessToken = jwtUtil.generateToken(userDetails);

        return new AuthResponseDto(newAccessToken, token.getToken());
    }

    // 🚪 LOGOUT
    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenRequestDto request) {

        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}

