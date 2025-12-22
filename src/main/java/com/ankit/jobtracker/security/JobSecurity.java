package com.ankit.jobtracker.security;

import com.ankit.jobtracker.repository.JobRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JobSecurity {

    private final JobRepository jobRepository;

    public JobSecurity(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

     public boolean isOwner(Long jobId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        return jobRepository.existsByIdAndOwnerUsername(jobId, username);
    }
}
