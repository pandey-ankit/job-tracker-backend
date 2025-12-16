package com.ankit.jobtracker.service;

import com.ankit.jobtracker.dto.JobPageResponseDto;
import com.ankit.jobtracker.dto.JobRequestDto;
import com.ankit.jobtracker.dto.JobResponseDto;
import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.repository.JobRepository;
import com.ankit.jobtracker.specification.JobSpecifications;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


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

    // DELETE – idempotent
    public void deleteJob(Long id) {

    Job job = jobRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Job not found with id: " + id));

    jobRepository.delete(job);
    }

    public JobPageResponseDto getJobs(Pageable pageable) {

    Page<Job> page = jobRepository.findAll(pageable);

    List<JobResponseDto> jobs = page.getContent()
            .stream()
            .map(this::mapToResponse)
            .toList();

    return new JobPageResponseDto(
            jobs,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
    );
    }

    public JobPageResponseDto searchJobs(
        String location,
        String keyword,
        Pageable pageable) {

    Specification<Job> spec = Specification.where(null);

    if (location != null && !location.isBlank()) {
        spec = spec.and(JobSpecifications.hasLocation(location));
    }

    if (keyword != null && !keyword.isBlank()) {
        spec = spec.and(JobSpecifications.hasKeyword(keyword));
    }

    Page<Job> page = jobRepository.findAll(spec, pageable);

    List<JobResponseDto> jobs = page.getContent()
            .stream()
            .map(this::mapToResponse)
            .toList();

    return new JobPageResponseDto(
            jobs,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
    );
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