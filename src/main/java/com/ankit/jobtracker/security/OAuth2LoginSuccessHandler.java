/* package com.ankit.jobtracker.security;

import com.ankit.jobtracker.entity.RefreshToken;
import com.ankit.jobtracker.repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil,
                                     RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        // Map Google user → local role
        var userDetails = new org.springframework.security.core.userdetails.User(
                email,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = jwtUtil.generateToken(userDetails);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(email);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        refreshTokenRepository.save(refreshToken);

        // Return tokens (JSON)
        response.setContentType("application/json");
        response.getWriter().write("""
        {
          "accessToken": "%s",
          "refreshToken": "%s"
        }
        """.formatted(accessToken, refreshToken.getToken()));
    }
}
    */