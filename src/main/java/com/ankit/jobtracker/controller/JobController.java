package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.JobRequestDto;
import com.ankit.jobtracker.dto.JobResponseDto;
import com.ankit.jobtracker.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobResponseDto createJob(@RequestBody JobRequestDto dto) {
        return jobService.createJob(dto);
    }

    @GetMapping
    public List<JobResponseDto> getAllJobs() {
        return jobService.getAllJobs();
    }

    // PUT – full update
    @PutMapping("/{id}")
    public JobResponseDto updateJob(
            @PathVariable Long id,
            @RequestBody JobRequestDto dto) {
        return jobService.updateJob(id, dto);
    }

    // PATCH – partial update
    @PatchMapping("/{id}")
    public JobResponseDto patchJob(
            @PathVariable Long id,
            @RequestBody JobRequestDto dto) {
        return jobService.patchJob(id, dto);
    }
}