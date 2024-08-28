package com.project.tempotalk.services.images;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.tempotalk.exceptions.FileUploadException;
import com.project.tempotalk.models.Album;
import com.project.tempotalk.models.Artist;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.ImageUploadRequest;
import com.project.tempotalk.payload.response.ImageUploadResponse;
import com.project.tempotalk.repositories.AlbumRepository;
import com.project.tempotalk.repositories.ArtistRepository;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

// Service layer for interacting with Images
@Service
@Slf4j
public class ImageServiceImpl implements ImageService{
    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.accessKey}")
    private String accessKey;
    @Value("${aws.s3.secretKey}")
    private String secretKey;
    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;

    private AmazonS3 s3client;

    // Initialize and build our AmazonS3 client where we store our images
    @PostConstruct
    private void initialize(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    // Upload an album cover image to Amazon S3 bucket
    @Override
    public ImageUploadResponse uploadAlbumImage(ImageUploadRequest imageUploadRequest){
        // If album doesn't exist then return an appropriate ImageUploadResponse
        if (!albumRepository.existsById(imageUploadRequest.getId())){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }

        // Once again, if album wasn't found then return an appropriate ImageUploadResponse
        Optional<Album> tempAlbum = albumRepository.findById(imageUploadRequest.getId());
        if (tempAlbum.isEmpty()){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }
        Album album = tempAlbum.get();

        // Get the image file from the ImageUploadRequest and attempt to upload it
        MultipartFile file = imageUploadRequest.getFile();
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        String folder = "coverArt";
        String filePath = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            filePath = folder + "/" + album.getId() + "_" + file.getOriginalFilename();
            s3client.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            imageUploadResponse.setFilePath(filePath);
            imageUploadResponse.setDateTime(LocalDateTime.now());
        } catch (IOException e){
            log.error("Error occurred ==> {}", e.getMessage());
            throw new FileUploadException("Error occurred in file upload ==> " + e.getMessage());
        }

        album.setCoverArt(endpointUrl + filePath);
        albumRepository.save(album);

        return imageUploadResponse;
    }

    // Upload a song cover image to Amazon S3 bucket
    @Override
    public ImageUploadResponse uploadSongImage(ImageUploadRequest imageUploadRequest){
        // If song doesn't exist then return an appropriate ImageUploadResponse
        if (!songRepository.existsById(imageUploadRequest.getId())){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }

        // Once again, if song wasn't found then return an appropriate ImageUploadResponse
        Optional<Song> tempSong = songRepository.findById(imageUploadRequest.getId());
        if (tempSong.isEmpty()){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }
        Song song = tempSong.get();

        // Get the image file from the ImageUploadRequest and attempt to upload it
        MultipartFile file = imageUploadRequest.getFile();
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        String folder = "coverArt";
        String filePath = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            filePath = folder + "/" + song.getId() + "_" + file.getOriginalFilename();
            s3client.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            imageUploadResponse.setFilePath(filePath);
            imageUploadResponse.setDateTime(LocalDateTime.now());
        } catch (IOException e){
            log.error("Error occurred ==> {}", e.getMessage());
            throw new FileUploadException("Error occurred in file upload ==> " + e.getMessage());
        }

        song.setCoverArt(endpointUrl + filePath);
        songRepository.save(song);

        return imageUploadResponse;
    }

    // Upload a profile picture image to Amazon S3 bucket
    @Override
    public ImageUploadResponse uploadProfileImage(ImageUploadRequest imageUploadRequest){
        // If user doesn't exist then return an appropriate ImageUploadResponse
        if (!userRepository.existsById(imageUploadRequest.getId())){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }

        // Once again, if user wasn't found then return an appropriate ImageUploadResponse
        Optional<User> tempUser = userRepository.findById(imageUploadRequest.getId());
        if (tempUser.isEmpty()){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }
        User user = tempUser.get();

        // Get the image file from the ImageUploadRequest and attempt to upload it
        MultipartFile file = imageUploadRequest.getFile();
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        String folder = "profileImages";
        String filePath = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            filePath = folder + "/" + user.getId() + "_" + file.getOriginalFilename();
            s3client.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            imageUploadResponse.setFilePath(filePath);
            imageUploadResponse.setDateTime(LocalDateTime.now());
        } catch (IOException e){
            log.error("Error occurred ==> {}", e.getMessage());
            throw new FileUploadException("Error occurred in file upload ==> " + e.getMessage());
        }

        user.setProfileImage(endpointUrl + filePath);
        userRepository.save(user);

        return imageUploadResponse;
    }

    // Upload an artist image to Amazon S3 bucket
    @Override
    public ImageUploadResponse uploadArtistImage(ImageUploadRequest imageUploadRequest){
        // If artist doesn't exist then return an appropriate ImageUploadResponse
        if (!artistRepository.existsById(imageUploadRequest.getId())){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }

        // Once again, if artist wasn't found then return an appropriate ImageUploadResponse
        Optional<Artist> tempArtist = artistRepository.findById(imageUploadRequest.getId());
        if (tempArtist.isEmpty()){
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setFilePath("Image was not uploaded: no file path created");
            imageUploadResponse.setDateTime(LocalDateTime.now());
            return imageUploadResponse;
        }
        Artist artist = tempArtist.get();

        // Get the image file from the ImageUploadRequest and attempt to upload it
        MultipartFile file = imageUploadRequest.getFile();
        ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
        String folder = "artistImages";
        String filePath = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            filePath = folder + "/" + artist.getId() + "_" + file.getOriginalFilename();
            s3client.putObject(bucketName, filePath, file.getInputStream(), objectMetadata);
            imageUploadResponse.setFilePath(filePath);
            imageUploadResponse.setDateTime(LocalDateTime.now());
        } catch (IOException e){
            log.error("Error occurred ==> {}", e.getMessage());
            throw new FileUploadException("Error occurred in file upload ==> " + e.getMessage());
        }

        artist.setArtistImage(endpointUrl + filePath);
        artistRepository.save(artist);

        return imageUploadResponse;
    }
}