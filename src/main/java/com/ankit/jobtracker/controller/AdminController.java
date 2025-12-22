package com.ankit.jobtracker.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHealth() {
        return "Admin access confirmed";
    }
}
