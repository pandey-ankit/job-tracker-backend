package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.LoginRequest;
import com.ankit.jobtracker.dto.LoginResponse;
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

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

    String token = jwtUtil.generateToken(authentication.getName(), roles);

    return new LoginResponse(
                token,
                "Bearer",
                authentication.getName(),
                roles
        );
        }

}