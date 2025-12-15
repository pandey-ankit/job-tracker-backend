package com.ankit.jobtracker.repository;

import com.ankit.jobtracker.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword,
            String descriptionKeyword,
            Pageable pageable
    );

    Page<Job> findByLocationIgnoreCaseAndTitleContainingIgnoreCaseOrLocationIgnoreCaseAndDescriptionContainingIgnoreCase(
            String location1,
            String titleKeyword,
            String location2,
            String descriptionKeyword,
            Pageable pageable
    );
}