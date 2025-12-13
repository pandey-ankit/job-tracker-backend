package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.ApiResponse;
import com.ankit.jobtracker.dto.JobRequestDTO;
import com.ankit.jobtracker.dto.JobResponseDTO;
import com.ankit.jobtracker.model.Job;
import com.ankit.jobtracker.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ApiResponse<List<JobResponseDTO>> getAllJobs() {

    return new ApiResponse<>(
            true,
            "Jobs fetched successfully",
            jobService.getAllJobs()
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
