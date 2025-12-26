package com.ankit.jobtracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("!test")
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimiter rateLimiter;

    private final LoginAttemptService loginAttemptService;

    public LoginRateLimitFilter(LoginRateLimiter rateLimiter,
                            LoginAttemptService loginAttemptService) {
    this.rateLimiter = rateLimiter;
    this.loginAttemptService = loginAttemptService;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String username = request.getParameter("username");

        if (username != null && loginAttemptService.isLocked(username)) {
        filterChain.doFilter(request, response);
        return;
        }

        if (!rateLimiter.allowRequest(clientIp)) {
            response.setStatus(429);
            response.getWriter().write("Too many login attempts. Try later.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}