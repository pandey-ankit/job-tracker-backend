package com.ankit.jobtracker.service;

import com.ankit.jobtracker.entity.Job;
import com.ankit.jobtracker.repository.JobRepository;
import com.ankit.jobtracker.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobServiceTest {

    private final JobRepository jobRepository = mock(JobRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JobService jobService = new JobService(jobRepository, userRepository);

    @Test
    void userShouldSeeOnlyOwnJobs() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        doReturn(authorities).when(authentication).getAuthorities();

        Job job = new Job();
        job.setTitle("User Job");

        Page<Job> page = new PageImpl<>(List.of(job));

        when(jobRepository.findByOwnerUsername_Username(eq("user1"), any(Pageable.class)))
                .thenReturn(page);

        Page<?> result = jobService.listJobs(authentication, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        verify(jobRepository).findByOwnerUsername_Username(eq("user1"), any(Pageable.class));
        verify(jobRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void adminShouldSeeAllJobs() {

        Authentication authentication = mock(Authentication.class);

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        doReturn(authorities).when(authentication).getAuthorities();

        Job job = new Job();
        job.setTitle("Admin Job");

        Page<Job> page = new PageImpl<>(List.of(job));

        when(jobRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<?> result = jobService.listJobs(authentication, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        verify(jobRepository).findAll(any(Pageable.class));
        verify(jobRepository, never()).findByOwnerUsername_Username(any(), any());
    }
}