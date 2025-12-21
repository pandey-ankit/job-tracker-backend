package com.ankit.jobtracker.dto;

import java.util.List;

public class LoginResponse {

    private String username;
    private List<String> roles;
    private String message;

    public LoginResponse(String username, List<String> roles, String message) {
        this.username = username;
        this.roles = roles;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getMessage() {
        return message;
    }
}