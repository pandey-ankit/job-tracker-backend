package com.ankit.jobtracker.service;

import com.ankit.jobtracker.dto.CreateJobRequest;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.repository.JobRepository;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * CREATE JOB
     * OWNER is derived from Authentication
     */


public Job createJob(CreateJobRequest request, Authentication authentication) {

    Job job = new Job();
    job.setTitle(request.getTitle());
    job.setDescription(request.getDescription());
    job.setLocation(request.getLocation());
    job.setOwnerUsername(authentication.getName());
    job.setCreatedAt(Instant.now());

    return jobRepository.save(job);
}


    /**
     * LIST JOBS (Paginated)
     * USER  -> own jobs
     * ADMIN -> all jobs
     */
    public Page<Job> listJobs(Authentication authentication, Pageable pageable) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return jobRepository.findAll(pageable);
        }

        return jobRepository.findByOwnerUsername(authentication.getName(), pageable);
    }

    /**
     * GET JOB BY ID
     */
    public Job getJobById(Long id) {
    return jobRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Job not found with id: " + id)
            );
    }

    /**
     * UPDATE JOB
     */
    public Job updateJob(Long id, Job updatedJob) {

        Job existing = getJobById(id);

        existing.setTitle(updatedJob.getTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setLocation(updatedJob.getLocation());

        return jobRepository.save(existing);
    }

    /**
     * DELETE JOB
     */
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}
