package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.AuthRequestDto;
import com.ankit.jobtracker.security.JwtUtil;
import com.ankit.jobtracker.security.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDto request) {

    System.out.println(">>> LOGIN API HIT <<<");

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword())
    );

    var userDetails =
            userDetailsService.loadUserByUsername(request.getUsername());

    return jwtUtil.generateToken(userDetails);
    }

}