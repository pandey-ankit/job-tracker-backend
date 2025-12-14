package com.ankit.jobtracker.service;

import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.model.Job;
import com.ankit.jobtracker.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ankit.jobtracker.dto.JobRequestDTO;
import com.ankit.jobtracker.dto.JobResponseDTO;
import com.ankit.jobtracker.dto.PagedResponse;
import com.ankit.jobtracker.enums.JobStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponseDTO createJob(JobRequestDTO dto) {

    Job job = Job.builder()
            .company(dto.getCompany())
            .role(dto.getRole())
            .status(dto.getStatus())
            .appliedDate(dto.getAppliedDate())
            .build();

    Job savedJob = jobRepository.save(job);

    return mapToResponse(savedJob);
    }


    public Job updateJob(Long id, Job updatedJob) {
    Job existingJob = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found with id " + id));

    existingJob.setCompany(updatedJob.getCompany());
    existingJob.setRole(updatedJob.getRole());
    existingJob.setStatus(updatedJob.getStatus());
    existingJob.setAppliedDate(updatedJob.getAppliedDate());

    return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
    if (!jobRepository.existsById(id)) {
        throw new ResourceNotFoundException("Job not found with id " + id);
    }
    jobRepository.deleteById(id);
    }


    public List<JobResponseDTO> getAllJobs() {
    return jobRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    private JobResponseDTO mapToResponse(Job job) {
    return new JobResponseDTO(
            job.getId(),
            job.getCompany(),
            job.getRole(),
            job.getStatus(),
            job.getAppliedDate()
    );
    }



    public Job getJobById(Long id) {
    return jobRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Job not found with id " + id));
    }

    public PagedResponse<JobResponseDTO> getJobs(
        int page,
        int size,
        String sortBy,
        String direction,
        JobStatus status,
        String company
    ) {

    Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Job> jobPage;

    if (status != null && company != null) {
        jobPage = jobRepository
                .findByStatusAndCompanyContainingIgnoreCase(status, company, pageable);
    } else if (status != null) {
        jobPage = jobRepository.findByStatus(status, pageable);
    } else if (company != null) {
        jobPage = jobRepository
                .findByCompanyContainingIgnoreCase(company, pageable);
    } else {
        jobPage = jobRepository.findAll(pageable);
    }

    List<JobResponseDTO> content = jobPage
            .getContent()
            .stream()
            .map(this::mapToResponse)
            .toList();

    return new PagedResponse<>(
            content,
            jobPage.getNumber(),
            jobPage.getSize(),
            jobPage.getTotalElements(),
            jobPage.getTotalPages()
    );
    }


}
