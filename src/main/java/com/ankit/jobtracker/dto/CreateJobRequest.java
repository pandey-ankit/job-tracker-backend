package com.ankit.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateJobRequest {

    @NotBlank(message = "title must not be blank")
    @Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "description must be at most 500 characters")
    private String description;

    @Size(max = 100, message = "location must be at most 100 characters")
    private String location;

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
