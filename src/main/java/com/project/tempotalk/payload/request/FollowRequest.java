package com.project.tempotalk.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

// Request object for UserController endpoints
@Data
@AllArgsConstructor
public class FollowRequest {
    @NotBlank
    private String followerId;
    @NotBlank
    private String followeeId;
}