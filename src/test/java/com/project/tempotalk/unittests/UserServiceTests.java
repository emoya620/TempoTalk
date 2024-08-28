package com.project.tempotalk.unittests;

import com.project.tempotalk.models.Album;
import com.project.tempotalk.models.Review;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.FollowRequest;
import com.project.tempotalk.payload.response.ReviewResponse;
import com.project.tempotalk.payload.response.UserResponse;
import com.project.tempotalk.repositories.AlbumRepository;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Unit tests for the UserService class
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private UserService userService;

    private User follower;
    private User followed;
    private Album album;
    private Song song;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        follower = new User("follower","temp@gmail.com","password");
        followed = new User("followed","temp@gmail.com","password");
        album = new Album("album", "artist", "releaseDate", new ArrayList<>());
        song = new Song("song", "artist", "releaseDate", new ArrayList<>());
    }

    // Tests for when a user being followed was not found
    @Test
    public void UserService_FollowUser_FollowedNotFound(){
        FollowRequest followRequest = new FollowRequest("followerId", "followeeId");
        when(userRepository.findById(followRequest.getFolloweeId())).thenReturn(Optional.empty());
        UserResponse response = userService.followUser(followRequest);
        assertThat(response.getUser()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: The user being followed was not found");
    }

    // Tests for when a user trying to follow another user was not found
    @Test
    public void UserService_FollowUser_FollowingNotFound(){
        FollowRequest followRequest = new FollowRequest("followerId", "followeeId");
        when(userRepository.findById(followRequest.getFolloweeId())).thenReturn(Optional.of(followed));
        when(userRepository.findById(followRequest.getFollowerId())).thenReturn(Optional.empty());
        UserResponse response = userService.followUser(followRequest);
        assertThat(response.getUser()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: The following user was not found");
    }

    // Tests for when a user is already following the user they indicated they wanted to follow
    @Test
    public void UserService_FollowUser_AlreadyFollowing(){
        follower.getFollowing().add(followed.getId());
        FollowRequest followRequest = new FollowRequest("followerId", "followeeId");
        when(userRepository.findById(followRequest.getFolloweeId())).thenReturn(Optional.of(followed));
        when(userRepository.findById(followRequest.getFollowerId())).thenReturn(Optional.of(follower));
        UserResponse response = userService.followUser(followRequest);
        assertThat(response.getUser()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error: User is already being followed");
    }

    // Tests for when a user successfully follows another user
    @Test
    public void UserService_FollowUser_FollowedSuccessfully(){
        FollowRequest followRequest = new FollowRequest("followerId", "followeeId");
        when(userRepository.findById(followRequest.getFolloweeId())).thenReturn(Optional.of(followed));
        when(userRepository.findById(followRequest.getFollowerId())).thenReturn(Optional.of(follower));
        UserResponse response = userService.followUser(followRequest);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getFollowee()).isNotNull();
        assertThat(response.getMessage()).isEqualTo("User followed successfully!");
    }

    // Tests for when a user trying to view their feed was not found
    @Test
    public void UserService_GetUserFeed_UserNotFound(){
        when(userRepository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());
        List<ReviewResponse> feed = userService.getUserFeed("userId");
        assertThat(feed.getFirst().getMessage()).isEqualTo("User was not found");
    }

    // Tests for when a user successfully gets their feed
    @Test
    public void UserService_GetUserFeed_Success(){
        List<Review> reviews = new ArrayList<>();
        List<String> following = new ArrayList<>();
        Review review = new Review("review", 100, "userId", "musicId");
        reviews.add(review);
        reviews.add(review);
        reviews.add(review);
        Criteria criteria = new Criteria();
        criteria.and("userId").in(following);
        Query query = new Query(criteria);
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, "creationDate")));

        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(follower));
        when(mongoTemplate.find(query, Review.class)).thenReturn(reviews);
        when(albumRepository.findById(Mockito.anyString())).thenReturn(Optional.of(album));
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));

        List<ReviewResponse> feed = userService.getUserFeed("userId");
        assertThat(feed).isNotEmpty();
    }
}