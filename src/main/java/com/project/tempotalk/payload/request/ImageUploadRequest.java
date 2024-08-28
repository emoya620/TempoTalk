package com.project.tempotalk.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// Request object for ImageController endpoints
@Data
@AllArgsConstructor
public class ImageUploadRequest {
    private MultipartFile file;
    private String id;
}