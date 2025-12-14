package com.ankit.jobtracker.repository;

import com.ankit.jobtracker.enums.JobStatus;
import com.ankit.jobtracker.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByCompanyContainingIgnoreCase(String company, Pageable pageable);

    Page<Job> findByStatusAndCompanyContainingIgnoreCase(
            JobStatus status,
            String company,
            Pageable pageable
    );
}