package com.ankit.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class JobTrackerApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(JobTrackerApplication.class, args);
    }
}