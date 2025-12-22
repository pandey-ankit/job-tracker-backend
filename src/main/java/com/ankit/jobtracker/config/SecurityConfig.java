package com.ankit.jobtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ankit.jobtracker.security.LoginRateLimitFilter;
//import com.ankit.jobtracker.security.OAuth2LoginSuccessHandler;
import com.ankit.jobtracker.security.OAuth2UserServiceImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.ankit.jobtracker.security.CustomUserDetailsService;

import org.springframework.security.authentication.ProviderManager;




@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ✅ ONLY inject the filter (no provider here)
    private final LoginRateLimitFilter loginRateLimitFilter;
    private final OAuth2UserServiceImpl oAuth2UserServiceImpl;
   // private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(LoginRateLimitFilter loginRateLimitFilter,
                      OAuth2UserServiceImpl oAuth2UserServiceImpl,
                      CustomUserDetailsService customUserDetailsService,
                      JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.loginRateLimitFilter = loginRateLimitFilter;
    this.oAuth2UserServiceImpl = oAuth2UserServiceImpl;
    this.customUserDetailsService = customUserDetailsService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    


     @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }



/* 
    // ✅ AuthenticationManager (no circular dependency)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
*/

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
    }

}
