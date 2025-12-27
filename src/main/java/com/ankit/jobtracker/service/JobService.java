package com.ankit.jobtracker.service;

import com.ankit.jobtracker.dto.CreateJobRequest;
import com.ankit.jobtracker.dto.JobResponse;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.enums.JobStatus;
import com.ankit.jobtracker.exception.InvalidJobStatusTransitionException;
import com.ankit.jobtracker.entity.User;
import com.ankit.jobtracker.repository.JobRepository;
import com.ankit.jobtracker.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    /**
     * CREATE JOB
     */
    public Job createJob(CreateJobRequest request, Authentication authentication) {

        User owner = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setOwnerUsername(owner);
        job.setStatus(JobStatus.CREATED);

        return jobRepository.save(job);
    }

    /**
     * LIST JOBS
     */
    public Page<JobResponse> listJobs(Authentication authentication, Pageable pageable) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Page<Job> jobs = isAdmin
                ? jobRepository.findAll(pageable)
                : jobRepository.findByOwnerUsername_Username(authentication.getName(), pageable);

        return jobs.map(this::toResponse);
    }

    /**
     * GET JOB BY ID (ownership enforced)
     */
    public Job getJobById(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
            !job.getOwnerUsername().getUsername().equals(auth.getName())) {
            throw new AccessDeniedException("You are not allowed to access this job");
        }

        return job;
    }

    public JobResponse getJobResponseById(Long id) {
        return toResponse(getJobById(id));
    }

    /**
     * UPDATE JOB (metadata only)
     */
    public JobResponse updateJob(Long id, Job updatedJob) {

        Job existing = getJobById(id);

        existing.setTitle(updatedJob.getTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setLocation(updatedJob.getLocation());

        jobRepository.save(existing);
        return toResponse(existing);
    }

    /**
     * UPDATE JOB STATUS (PATCH)
     */
    public void updateJobStatus(Long jobId, JobStatus newStatus) {

        Job job = getJobById(jobId); // ownership + admin enforced

        JobStatus current = job.getStatus();

        if (!isValidTransition(current, newStatus)) {
            throw new InvalidJobStatusTransitionException(
                "Invalid job status transition: " + current + " → " + newStatus
            );
        }

        job.setStatus(newStatus);
        jobRepository.save(job);
    }

    /**
     * DELETE JOB (soft delete)
     */
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        jobRepository.delete(job); // @SQLDelete handles soft delete
    }

    /* ---------------- PRIVATE HELPERS ---------------- */

    private boolean isValidTransition(JobStatus from, JobStatus to) {

        if (from == JobStatus.ACCEPTED || from == JobStatus.REJECTED) {
            return false;
        }

        if (to == JobStatus.WITHDRAWN) {
            return true;
        }

        return switch (from) {
            case CREATED -> to == JobStatus.APPLIED;
            case APPLIED -> to == JobStatus.INTERVIEWING || to == JobStatus.REJECTED;
            case INTERVIEWING -> to == JobStatus.OFFERED || to == JobStatus.REJECTED;
            case OFFERED -> to == JobStatus.ACCEPTED || to == JobStatus.REJECTED;
            default -> false;
        };
    }

    private JobResponse toResponse(Job job) {
    return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getDescription(),
            job.getLocation(),
            job.getCreatedAt(),
            job.getStatus()
    );
}
}
