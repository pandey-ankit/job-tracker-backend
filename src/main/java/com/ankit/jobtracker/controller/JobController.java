package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.CreateJobRequest;
import com.ankit.jobtracker.dto.JobPageResponseDto;
import com.ankit.jobtracker.dto.JobRequestDto;
import com.ankit.jobtracker.dto.JobResponse;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.repository.JobRepository;
import com.ankit.jobtracker.service.JobService;

import jakarta.validation.Valid;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<JobResponse> listJobs(
        Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
    String[] sortParams = sort.split(",");
    Sort.Direction direction =
            sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

    Pageable pageable = PageRequest.of(
            page,
            Math.min(size, 50), // 🔒 prevent abuse
            Sort.by(direction, sortParams[0])
    );

    return jobService.listJobs(authentication, pageable);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @jobSecurity.isOwner(#id, authentication)")
    public JobResponse getJob(@PathVariable Long id) {
        return jobService.getJobResponseById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Job createJob(
            @Valid @RequestBody CreateJobRequest request,
            Authentication authentication
    ) {
        return jobService.createJob(request, authentication);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @jobSecurity.isOwner(#id, authentication)")
    public Job updateJob(@PathVariable Long id, @RequestBody Job job) {
        return jobService.updateJob(id, job);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
    }
}
