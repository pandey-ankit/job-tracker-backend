package com.ankit.jobtracker.controller;

import com.ankit.jobtracker.dto.JobPageResponseDto;
import com.ankit.jobtracker.dto.JobRequestDto;
import com.ankit.jobtracker.dto.JobResponseDto;
import com.ankit.jobtracker.service.JobService;

import jakarta.validation.Valid;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public JobPageResponseDto getJobs(
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String keyword,
        @PageableDefault(
                page = 0,
                size = 5,
                sort = "id",
                direction = Sort.Direction.ASC
        ) Pageable pageable) {

    return jobService.searchJobs(location, keyword, pageable);
    }
  


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponseDto createJob(@Valid @RequestBody JobRequestDto dto) {
        return jobService.createJob(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponseDto updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto dto) {
        return jobService.updateJob(id, dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponseDto patchJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto dto) {
        return jobService.patchJob(id, dto);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
    }

}