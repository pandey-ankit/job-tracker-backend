package com.ankit.jobtracker.service;

import com.ankit.jobtracker.exception.ResourceNotFoundException;
import com.ankit.jobtracker.model.Job;
import com.ankit.jobtracker.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
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


    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
    return jobRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Job not found with id " + id));
    }

}
