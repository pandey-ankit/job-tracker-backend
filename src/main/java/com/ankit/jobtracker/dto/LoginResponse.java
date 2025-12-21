package com.ankit.jobtracker.dto;

import java.util.List;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String username;
    private List<String> roles;

    public LoginResponse(String accessToken, String refreshToken,
                         String tokenType, String username, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.username = username;
        this.roles = roles;
    }

    // getters
    public String getRefreshToken() {
        return refreshToken;
    }

    // getters


    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public String getTokenType() {
        return tokenType;
    }

}