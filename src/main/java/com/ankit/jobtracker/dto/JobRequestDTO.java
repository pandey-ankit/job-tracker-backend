package com.ankit.jobtracker.dto;

import com.ankit.jobtracker.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class JobRequestDTO {

    @NotBlank
    private String company;

    @NotBlank
    private String role;

    @NotNull
    private JobStatus status;

    @NotNull
    private LocalDate appliedDate;

    // getters & setters
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }
}
