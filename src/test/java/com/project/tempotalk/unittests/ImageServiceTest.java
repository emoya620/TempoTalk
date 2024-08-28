package com.project.tempotalk.unittests;

import com.amazonaws.services.s3.AmazonS3;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.ImageUploadRequest;
import com.project.tempotalk.payload.response.ImageUploadResponse;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.services.images.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Unit tests for the ImageServiceImpl class
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3 s3client;

    @InjectMocks
    private ImageServiceImpl imageService;

    private MockMultipartFile file;
    private User user;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        user = new User("username", "email@gmail.com", "password");
    }

    // Tests for when a user is not found during the first search
    @Test
    public void ImageService_UploadProfileImage_UserNotFound1(){
        ImageUploadRequest request = new ImageUploadRequest(file, "id");
        when(userRepository.existsById(request.getId())).thenReturn(false);
        ImageUploadResponse response = imageService.uploadProfileImage(request);
        assertThat(response.getFilePath()).isEqualTo("Image was not uploaded: no file path created");
        assertThat(response.getDateTime()).isNotNull();
    }

    // Tests for when a user is not found during the second search
    @Test
    public void ImageService_UploadProfileImage_UserNotFound2(){
        ImageUploadRequest request = new ImageUploadRequest(file, "id");
        when(userRepository.existsById(request.getId())).thenReturn(true);
        when(userRepository.findById(request.getId())).thenReturn(Optional.empty());
        ImageUploadResponse response = imageService.uploadProfileImage(request);
        assertThat(response.getFilePath()).isEqualTo("Image was not uploaded: no file path created");
        assertThat(response.getDateTime()).isNotNull();
    }

    // Tests for when an image is uploaded successfully (will implement later, currently having issues with mocking the AmazonS3 client)
//    @Test
//    public void ImageService_UploadProfileImage_UploadSuccessful() throws IOException {
//        ImageUploadRequest request = new ImageUploadRequest(file, "id");
//        when(userRepository.existsById(request.getId())).thenReturn(true);
//        when(userRepository.findById(request.getId())).thenReturn(Optional.of(user));
//        when(s3client.putObject(null,"profileImages/null_filename.txt", file.getInputStream(), new ObjectMetadata())).thenReturn(new PutObjectResult());
//        ImageUploadResponse response = imageService.uploadProfileImage(request);
//        assertThat(response.getFilePath()).isNotNull();
//        assertThat(response.getDateTime()).isNotNull();
//    }
}