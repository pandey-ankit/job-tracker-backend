package com.ankit.jobtracker.dto;

import java.time.Instant;

public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Instant createdAt;

    public JobResponse(
            Long id,
            String title,
            String description,
            String location,
            Instant createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
