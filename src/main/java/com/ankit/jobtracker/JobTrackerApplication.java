package com.ankit.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
@EnableJpaAuditing
public class JobTrackerApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(JobTrackerApplication.class, args);
    }

   

    // 🔐 TEMPORARY: Generate BCrypt password on startup
    @Bean
    CommandLineRunner generateAdminPassword(PasswordEncoder passwordEncoder) {
        return args -> {
            String rawPassword = "user123";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            System.out.println("======================================");
            System.out.println("RAW PASSWORD     : " + rawPassword);
            System.out.println("BCRYPT PASSWORD  : " + encodedPassword);
            System.out.println("MATCHES TEST     : " +
                    passwordEncoder.matches(rawPassword, encodedPassword));
            System.out.println("======================================");
        };
    }
    

}