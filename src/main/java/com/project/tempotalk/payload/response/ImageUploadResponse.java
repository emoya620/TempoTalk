package com.project.tempotalk.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

// Response object for ImageController endpoints
@Data
public class ImageUploadResponse {
    private String filePath;
    private LocalDateTime dateTime;
}