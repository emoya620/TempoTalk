package com.project.tempotalk.unittests;

import com.project.tempotalk.models.Album;
import com.project.tempotalk.models.Review;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.EditReviewRequest;
import com.project.tempotalk.payload.request.ReviewRequest;
import com.project.tempotalk.payload.response.ReviewResponse;
import com.project.tempotalk.repositories.AlbumRepository;
import com.project.tempotalk.repositories.ReviewRepository;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.services.ReviewService;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Unit tests for the ReviewService class
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.LENIENT);

    @Mock
    UserRepository userRepository;

    @Mock
    AlbumRepository albumRepository;

    @Mock
    SongRepository songRepository;

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewService reviewService;

    private Album album;
    private Song song;
    private User user;
    private Review review;
    private List<String> userReviews;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        album = new Album("Wall of Eyes", "The Smile", "12-01-01", new ArrayList<>(Arrays.asList("Alternative Rock")));
        song = new Song("title", "artist", "releaseDate", new ArrayList<>(Arrays.asList("Alternative Rock")));
        user = new User("user", "temp@gmail.com", "password");
        review = new Review("body", 100, "userId", "musicId");
        userReviews = new ArrayList<>(Arrays.asList("reviewId1", "reviewId2"));
    }

    // Tests for when a user creating a new review was not found
    @Test
    public void ReviewService_CreateReview_UserNotFoundResponse(){
        ReviewRequest reviewRequest = new ReviewRequest("body", 100, "userId", "musicId");
        when(userRepository.findById(reviewRequest.getUserId())).thenReturn(Optional.empty());
        ReviewResponse response = reviewService.createReview(reviewRequest);
        assertThat(response.getReview()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: User not found");
    }

    // Tests for when a song or album being reviewed were not found
    @Test
    public void ReviewService_CreateReview_MusicNotFoundResponse(){
        ReviewRequest reviewRequest = new ReviewRequest("body", 100, "userId", "musicId");
        when(userRepository.findById(reviewRequest.getUserId())).thenReturn(Optional.of(new User()));
        when(albumRepository.existsById(reviewRequest.getMusicId())).thenReturn(false);
        when(songRepository.existsById(reviewRequest.getMusicId())).thenReturn(false);
        ReviewResponse response = reviewService.createReview(reviewRequest);
        assertThat(response.getReview()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: no album or song was found");
    }

    // Tests for when a review by a given user already exists for an album or song
    @Test
    public void ReviewService_CreateReview_ReviewAlreadyCreatedResponse(){
        ReviewRequest reviewRequest = new ReviewRequest("body", 100, "userId", "musicId");
        album.setReviews(new ArrayList<>(List.of("reviewId1")));
        user.setReviews(userReviews);
        when(userRepository.findById(reviewRequest.getUserId())).thenReturn(Optional.of(user));
        when(albumRepository.existsById(reviewRequest.getMusicId())).thenReturn(true);
        when(albumRepository.findById(reviewRequest.getMusicId())).thenReturn(Optional.of(album));
        ReviewResponse response = reviewService.createReview(reviewRequest);
        assertThat(response.getReview()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: User has already created a review for this music");
    }

    // Tests for when a review is successfully created
    @Test
    public void ReviewService_CreateReview_ReviewSuccessfullyCreatedResponse (){
        ReviewRequest reviewRequest = new ReviewRequest("body", 100, "userId", "musicId");
        album.setReviews(new ArrayList<>(List.of("reviewId")));
        user.setReviews(userReviews);
        when(userRepository.findById(reviewRequest.getUserId())).thenReturn(Optional.of(user));
        when(albumRepository.existsById(reviewRequest.getMusicId())).thenReturn(true);
        when(albumRepository.findById(reviewRequest.getMusicId())).thenReturn(Optional.of(album));
        when(reviewRepository.findById("reviewId")).thenReturn(Optional.of(review));
        ReviewResponse response = reviewService.createReview(reviewRequest);
        assertThat(response.getReview()).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Review created successfully!");
    }

    // Tests for when a review being updated was not found during the first search
    @Test
    public void ReviewService_UpdateReview_ReviewNotFound(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(false);
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Error: Review was not found");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a review being updated was not found during the second search
    @Test
    public void ReviewService_UpdateReview_ReviewNotFound2(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.empty());
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Error: Review was not found");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when an album or song associated with the updated review was not found during the first search
    @Test
    public void ReviewService_UpdateReview_MusicNotFound(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(false);
        when(songRepository.existsById(Mockito.anyString())).thenReturn(false);
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Error: Album or song was not found");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when an album or song associated with the updated review was not found during the second search
    @Test
    public void ReviewService_UpdateReview_MusicNotFound2(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Error: Review was not associated with an album or song");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a user associated with the updated review was not found
    @Test
    public void ReviewService_UpdateReview_UserNotFound(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Error: User not found");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a review for an album is updated successfully
    @Test
    public void ReviewService_UpdateReview_Success(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.findById(Mockito.anyString())).thenReturn(Optional.of(album));
        when(albumRepository.save(Mockito.any(Album.class))).thenReturn(album);
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Review updated successfully!");
        assertThat(response.getReview()).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getAlbum()).isNotNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a review for a song is updated successfully
    @Test
    public void ReviewService_UpdateReview_Success2(){
        EditReviewRequest request = new EditReviewRequest("body", 100, "reviewId", "musicId");
        when(reviewRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(songRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);
        when(songRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        when(songRepository.save(Mockito.any(Song.class))).thenReturn(song);
        ReviewResponse response = reviewService.updateReview(request);
        assertThat(response.getMessage()).isEqualTo("Review updated successfully!");
        assertThat(response.getReview()).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getSong()).isNotNull();
        assertThat(response.getAlbum()).isNull();
    }

    // Tests for when a review being deleted was not found
    @Test
    public void ReviewService_DeleteReview_ReviewNotFound(){
        when(reviewRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ReviewResponse response = reviewService.deleteReview("reviewId");
        assertThat(response.getMessage()).isEqualTo("Error: Review was not found");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a user associated with a review being deleted was not found
    @Test
    public void ReviewService_DeleteReview_UserNotFound(){
        when(reviewRepository.findById(Mockito.anyString())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ReviewResponse response = reviewService.deleteReview("reviewId");
        assertThat(response.getMessage()).isEqualTo("Error: Review was not associated with a user");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when an album or song associated with a review being deleted was not found
    @Test
    public void ReviewService_DeleteReview_MusicNotFound(){
        when(reviewRepository.findById(Mockito.anyString())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        ReviewResponse response = reviewService.deleteReview("reviewId");
        assertThat(response.getMessage()).isEqualTo("Error: Review was not associated with an album or song");
        assertThat(response.getReview()).isNull();
        assertThat(response.getUser()).isNull();
        assertThat(response.getAlbum()).isNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a review for an album is deleted successfully
    @Test
    public void ReviewService_DeleteReview_Success(){
        when(reviewRepository.findById(Mockito.anyString())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(albumRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(albumRepository.findById(Mockito.anyString())).thenReturn(Optional.of(album));
        ReviewResponse response = reviewService.deleteReview("reviewId");
        assertThat(response.getMessage()).isEqualTo("Review deleted successfully!");
        assertThat(response.getReview()).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getAlbum()).isNotNull();
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a review for a song is deleted successfully
    @Test
    public void ReviewService_DeleteReview_Success2(){
        when(reviewRepository.findById(Mockito.anyString())).thenReturn(Optional.of(review));
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(songRepository.existsById(Mockito.anyString())).thenReturn(true);
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        ReviewResponse response = reviewService.deleteReview("reviewId");
        assertThat(response.getMessage()).isEqualTo("Review deleted successfully!");
        assertThat(response.getReview()).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getSong()).isNotNull();
        assertThat(response.getAlbum()).isNull();
    }
}