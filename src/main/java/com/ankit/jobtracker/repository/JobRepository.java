package com.ankit.jobtracker.repository;

import com.ankit.jobtracker.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByOwnerUsername_Username(String username, Pageable pageable);

    Page<Job> findAll(Pageable pageable);

    boolean existsByIdAndOwnerUsername_Username(Long id, String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM job", nativeQuery = true)
    void hardDeleteAll();
}


