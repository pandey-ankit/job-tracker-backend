package com.ankit.jobtracker.dto;

import com.ankit.jobtracker.enums.JobStatus;
import java.time.LocalDate;

public class JobResponseDTO {

    private Long id;
    private String company;
    private String role;
    private JobStatus status;
    private LocalDate appliedDate;

    public JobResponseDTO(Long id, String company, String role,
                          JobStatus status, LocalDate appliedDate) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    public Long getId() { return id; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public JobStatus getStatus() { return status; }
    public LocalDate getAppliedDate() { return appliedDate; }
}
