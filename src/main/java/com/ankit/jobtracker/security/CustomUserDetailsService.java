package com.ankit.jobtracker.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {

        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password("$2a$10$j0Ile91FAlRXqgUzw3ySe.X37KVRaGCgXCxdrmoqznFZ.dkmxRC3u")
                    .roles("ADMIN")
                    .build();
        }

        if ("user".equals(username)) {
            return User.withUsername("user")
                    .password("$2a$10$sDxv2V6iuKITWYnZh/xo.OsECF/L5jBiubwM1j0LAj/c6wRdcZH0W")
                    .roles("USER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found");
    }
}