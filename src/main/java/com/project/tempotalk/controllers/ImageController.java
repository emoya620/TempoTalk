package com.project.tempotalk.controllers;

import com.project.tempotalk.payload.request.ImageUploadRequest;
import com.project.tempotalk.payload.response.ImageUploadResponse;
import com.project.tempotalk.services.images.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    ImageService imageService;

    // Endpoint for uploading an album cover image
    @PostMapping("/uploadAlbum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadAlbumFile(ImageUploadRequest imageUploadRequest) {
        return new ResponseEntity<>(imageService.uploadAlbumImage(imageUploadRequest), HttpStatus.OK);
    }

    // Endpoint for uploading a song cover image
    @PostMapping("/uploadSong")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadSongFile(ImageUploadRequest imageUploadRequest) {
        return new ResponseEntity<>(imageService.uploadSongImage(imageUploadRequest), HttpStatus.OK);
    }

    // Endpoint for uploading a profile image
    @PostMapping("/uploadProfile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadProfileFile(ImageUploadRequest imageUploadRequest) {
        return new ResponseEntity<>(imageService.uploadProfileImage(imageUploadRequest), HttpStatus.OK);
    }

    // Endpoint for uploading an artist image
    @PostMapping("/uploadArtist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadArtistFile(ImageUploadRequest imageUploadRequest) {
        return new ResponseEntity<>(imageService.uploadArtistImage(imageUploadRequest), HttpStatus.OK);
    }
}