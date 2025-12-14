package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.ApiResponse;
import com.ankit.jobtracker.dto.JobRequestDTO;
import com.ankit.jobtracker.dto.JobResponseDTO;
import com.ankit.jobtracker.dto.PagedResponse;
import com.ankit.jobtracker.model.Job;
import com.ankit.jobtracker.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

import com.ankit.jobtracker.enums.JobStatus;


@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ApiResponse<PagedResponse<JobResponseDTO>> getJobs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "appliedDate") String sortBy,
        @RequestParam(defaultValue = "desc") String direction,
        @RequestParam(required = false) JobStatus status,
        @RequestParam(required = false) String company
    ) {

    return new ApiResponse<>(
            true,
            "Jobs fetched successfully",
            jobService.getJobs(page, size, sortBy, direction, status, company)
    );
    }



    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
    return jobService.getJobById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id) {
    jobService.deleteJob(id);
    }


    @PostMapping
    public ApiResponse<JobResponseDTO> createJob(
        @Valid @RequestBody JobRequestDTO dto) {

    return new ApiResponse<>(
            true,
            "Job created successfully",
            jobService.createJob(dto)
    );
    }


    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @Valid @RequestBody Job job) {
    return jobService.updateJob(id, job);
    }

}
