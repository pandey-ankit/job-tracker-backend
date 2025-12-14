package com.ankit.jobtracker.dto;

public class JobResponseDto {

    private Long id;
    private String title;
    private String description;
    private String location;

    public JobResponseDto() {
    }

    public JobResponseDto(Long id, String title, String description, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
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
}