package com.ankit.jobtracker.dto;

import com.ankit.jobtracker.enums.JobStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateJobStatusRequest {

    @NotNull
    private JobStatus status;

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}