package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.LoginRequest;
import com.ankit.jobtracker.dto.LoginResponse;
import com.ankit.jobtracker.dto.RefreshTokenRequest;
import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.security.RefreshTokenService;

import org.springframework.http.ResponseEntity;
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

        @SuppressWarnings("unchecked")
        @PostMapping("/login")
        public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );
       
        User user = (User) authentication.getPrincipal();
        List<String> roles = (List<String>) user.getRoles();

        String accessToken = jwtUtil.generateToken(user);
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
        public String refresh(@RequestBody RefreshTokenRequest request) {
        String accessToken = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        return accessToken;
        }


        @PostMapping("/logout")
        public String logout(@RequestBody RefreshTokenRequest request) {
        System.out.println(">>> HIT /auth/logout with token = " + request.getRefreshToken());
        refreshTokenService.revokeToken(request.getRefreshToken());
        return "Logged out successfully";
        }




}