package com.ankit.jobtracker.repository;

import com.ankit.jobtracker.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository
        extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
}
