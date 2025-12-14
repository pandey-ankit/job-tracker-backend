package com.ankit.jobtracker.service;

import com.ankit.jobtracker.dto.JobRequestDto;
import com.ankit.jobtracker.dto.JobResponseDto;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // CREATE
    public JobResponseDto createJob(JobRequestDto dto) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());

        return mapToResponse(jobRepository.save(job));
    }

    // READ
    public List<JobResponseDto> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // PUT – FULL UPDATE (NEW)
    public JobResponseDto updateJob(Long id, JobRequestDto dto) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());

        return mapToResponse(jobRepository.save(job));
    }

    // PATCH – PARTIAL UPDATE (NEW)
    public JobResponseDto patchJob(Long id, JobRequestDto dto) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        if (dto.getTitle() != null) {
            job.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            job.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            job.setLocation(dto.getLocation());
        }

        return mapToResponse(jobRepository.save(job));
    }

    // Mapping (unchanged)
    private JobResponseDto mapToResponse(Job job) {
        return new JobResponseDto(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation()
        );
    }
}