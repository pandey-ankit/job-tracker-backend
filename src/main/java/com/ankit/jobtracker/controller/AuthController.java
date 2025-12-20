package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.AuthRequestDto;
import com.ankit.jobtracker.dto.AuthResponseDto;
import com.ankit.jobtracker.dto.RefreshTokenRequestDto;
import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import com.ankit.jobtracker.security.JwtUtil;
import com.ankit.jobtracker.security.LoginAttemptService;
import com.ankit.jobtracker.security.CustomUserDetailsService;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;
import com.ankit.jobtracker.security.CaptchaService;
import com.ankit.jobtracker.security.OtpService;
import com.ankit.jobtracker.dto.OtpVerifyRequestDto;




@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;
    private final CaptchaService captchaService;
    private final OtpService otpService;




    public AuthController(AuthenticationManager authenticationManager,
                      JwtUtil jwtUtil,
                      CustomUserDetailsService userDetailsService,
                      RefreshTokenRepository refreshTokenRepository,
                      LoginAttemptService loginAttemptService,
                      CaptchaService captchaService,
                      OtpService otpService) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.refreshTokenRepository = refreshTokenRepository;
    this.loginAttemptService = loginAttemptService;
    this.captchaService = captchaService;
    this.otpService = otpService;
    }



    // 🔐 LOGIN
    @PostMapping("/login")
    public void login(@RequestBody AuthRequestDto request) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword())
    );

    otpService.generateOtp(request.getUsername());
    }


    @PostMapping("/verify-otp")
    public AuthResponseDto verifyOtp(@RequestBody OtpVerifyRequestDto request) {

    boolean valid = otpService.verifyOtp(
            request.getUsername(),
            request.getOtp()
    );

    if (!valid) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired OTP"
        );
    }

    var userDetails =
            userDetailsService.loadUserByUsername(request.getUsername());

    String accessToken = jwtUtil.generateToken(userDetails);

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setUsername(request.getUsername());
    refreshToken.setExpiryDate(
            Instant.now().plusSeconds(7 * 24 * 60 * 60)
    );

    refreshTokenRepository.save(refreshToken);

    return new AuthResponseDto(accessToken, refreshToken.getToken());
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

