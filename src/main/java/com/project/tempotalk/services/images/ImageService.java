package com.project.tempotalk.services.images;

import com.project.tempotalk.payload.request.ImageUploadRequest;
import com.project.tempotalk.payload.response.ImageUploadResponse;

// Interface for our ImageServiceImpl class
public interface ImageService {
    ImageUploadResponse uploadAlbumImage(ImageUploadRequest imageUploadRequest);
    ImageUploadResponse uploadSongImage(ImageUploadRequest imageUploadRequest);
    ImageUploadResponse uploadProfileImage(ImageUploadRequest imageUploadRequest);
    ImageUploadResponse uploadArtistImage(ImageUploadRequest imageUploadRequest);
}