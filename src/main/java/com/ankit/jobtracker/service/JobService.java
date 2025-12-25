package com.ankit.jobtracker.service;

import com.ankit.jobtracker.dto.CreateJobRequest;
import com.ankit.jobtracker.dto.JobResponse;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.repository.JobRepository;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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


    public String getCurrentUsername() {
    return SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
    }



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

    public Page<JobResponse> listJobs(Authentication authentication, Pageable pageable) {

    boolean isAdmin = authentication.getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    Page<Job> jobs = isAdmin
            ? jobRepository.findAll(pageable)
            : jobRepository.findByOwnerUsername(authentication.getName(), pageable);

    return jobs.map(this::toResponse);
    }


    /**
     * GET JOB BY ID
     */

   public Job getJobById(Long jobId) {

    Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    if (isAdmin) {
        return job;
    }

    String currentUser = auth.getName();

    if (!job.getOwnerUsername().equals(currentUser)) {
        throw new AccessDeniedException("You are not allowed to view this job");
    }

    return job;
    }

    
   public JobResponse getJobResponseById(Long id) {
    Job job = getJobById(id);
    return toResponse(job);
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

    private JobResponse toResponse(Job job) {
    return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getDescription(),
            job.getLocation(),
            job.getCreatedAt()
    );
}
}