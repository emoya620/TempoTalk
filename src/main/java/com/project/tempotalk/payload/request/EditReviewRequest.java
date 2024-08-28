package com.project.tempotalk.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

// Request object for ReviewController endpoints
@Data
@AllArgsConstructor
public class EditReviewRequest {
    private String body;
    private int rating;
    private String reviewId;
    private String musicId;
}