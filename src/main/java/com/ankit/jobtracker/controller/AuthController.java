package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.LoginRequest;
import com.ankit.jobtracker.dto.LoginResponse;
import com.ankit.jobtracker.dto.RefreshTokenRequest;
import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.security.RefreshTokenService;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import com.ankit.jobtracker.security.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

        @PostMapping("/login")
        public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );

        List<String> roles = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        String accessToken = jwtUtil.generateToken(authentication.getName(), roles);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

        return new LoginResponse(
            accessToken,
            refreshToken.getToken(),
            "Bearer",
            authentication.getName(),
            roles
        );
        }

        @PostMapping("/refresh")
        public LoginResponse refresh(@RequestBody RefreshTokenRequest request) {
        System.out.println(">>> HIT /auth/refresh with token = " + request.getRefreshToken());
        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(request.getRefreshToken());

        String newAccessToken =
                jwtUtil.generateToken(refreshToken.getUsername(), List.of());

        return new LoginResponse(
                newAccessToken,
                request.getRefreshToken(),
                "Bearer",
                refreshToken.getUsername(),
                List.of()
        );
        }

        @PostMapping("/logout")
        public String logout(@RequestBody RefreshTokenRequest request) {
        System.out.println(">>> HIT /auth/logout with token = " + request.getRefreshToken());
        refreshTokenService.revokeToken(request.getRefreshToken());
        return "Logged out successfully";
        }




}