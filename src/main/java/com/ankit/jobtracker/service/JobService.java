package com.ankit.jobtracker.service;

import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.model.Job;
import com.ankit.jobtracker.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ankit.jobtracker.dto.JobRequestDTO;
import com.ankit.jobtracker.dto.JobResponseDTO;


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

}
