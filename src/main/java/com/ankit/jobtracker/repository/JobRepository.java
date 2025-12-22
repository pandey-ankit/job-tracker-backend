package com.ankit.jobtracker.repository;

import com.ankit.jobtracker.entity.Job;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByOwnerUsername(String ownerUsername, Pageable pageable);

    Page<Job> findAll(Pageable pageable);

    boolean existsByIdAndOwnerUsername(Long jobId, String username);
}


