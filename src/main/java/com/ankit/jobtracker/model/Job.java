package com.ankit.jobtracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.ankit.jobtracker.enums.JobStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company is required")
    private String company;
    
    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private JobStatus status;
   // APPLIED, INTERVIEW, OFFER, REJECTED

    @NotNull(message = "Applied Date is required")
    private LocalDate appliedDate;
}
