package com.project.tempotalk.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Service layer for interacting with Users
@Service
public class UserService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    // Return a list of all users in userRepository
    public List<User> allUsers(){
        return userRepository.findAll();
    }

    // Return a User object by their userId, if they exist
    public UserResponse userById(String userId){
        Optional<User> tempUser = userRepository.findById(userId);
        if (tempUser.isEmpty()){
            return new UserResponse("Error: User was not found");
        }
        User user = tempUser.get();

        return new UserResponse(user, "User found successfully!");
    }

    // Return a User object by their username, if they exist
    public UserResponse userByUsername(String username){
        Optional<User> tempUser = userRepository.findByUsername(username);
        if (tempUser.isEmpty()){
            return new UserResponse("Error: User was not found");
        }
        User user = tempUser.get();

        return new UserResponse(user,"User found successfully!");
    }

    // Follow a user and update following list
    public UserResponse followUser(FollowRequest followRequest){
        // Find the user who is being followed
        Optional<User> tempUser = userRepository.findById(followRequest.getFolloweeId());
        if (tempUser.isEmpty()){
            return new UserResponse("Error: The user being followed was not found");
        }
        User followed = tempUser.get();

        // Find the user who is following another user
        tempUser = userRepository.findById(followRequest.getFollowerId());
        if (tempUser.isEmpty()){
            return new UserResponse("Error: The following user was not found");
        }
        User follower = tempUser.get();
        List<String> following = follower.getFollowing();

        // Make sure the follower isn't already following the followee
        if (following.contains(followed.getId())){
            return new UserResponse("Error: User is already being followed");
        }

        // Update following list of the following user
        following.add(followed.getId());
        follower.setFollowing(following);
        userRepository.save(follower);

        return new UserResponse(follower, followed,"User followed successfully!");
    }

    // Unfollow a user and update following list
    public UserResponse unfollowUser(FollowRequest followRequest){
        // Find the user who is being followed
        Optional<User> tempUser = userRepository.findById(followRequest.getFolloweeId());
        if (tempUser.isEmpty()){
            return new UserResponse("Error: The user being unfollowed was not found");
        }
        User followed = tempUser.get();

        // Find the user who is following another user
        tempUser = userRepository.findById(followRequest.getFollowerId());
        if (tempUser.isEmpty()){
            return new UserResponse("Error: The unfollowing user was not found");
        }
        User follower = tempUser.get();
        List<String> following = follower.getFollowing();

        // Make sure the follower isn't already following the followee
        if (!following.contains(followed.getId())){
            return new UserResponse("Error: User wasn't being followed");
        }

        // Update following list of the following user
        following.remove(followed.getId());
        follower.setFollowing(following);
        userRepository.save(follower);

        return new UserResponse(follower, followed,"User was unfollowed successfully!");
    }

    // Get followed users
    public UserResponse getFollowedUsers(String userId){
        List<User> followedUsers = new ArrayList<>();

        // If user was not found then return an appropriate UserResponse
        Optional<User> tempUser = userRepository.findById(userId);
        if (tempUser.isEmpty()){
            return new UserResponse("Error: User was not found");
        }
        User user = tempUser.get();
        List<String> followingIds = user.getFollowing();

        // Find each User by their ID and add them to followedUsers array
        for (String id : followingIds){
            Optional<User> curUser = userRepository.findById(id);
            if (curUser.isPresent()){
                followedUsers.add(curUser.get());
            }
        }

        return new UserResponse(user, followedUsers,"Feed found successfully!");
    }

    // Get a feed of reviews from other users that the requesting user follows
    public List<ReviewResponse> getUserFeed(String userId){
        List<ReviewResponse> feed = new ArrayList<>();

        // If User was not found then return feed with one ReviewResponse indicating the user was not found
        Optional<User> tempUser = userRepository.findById(userId);
        if (tempUser.isEmpty()){
            ReviewResponse response = new ReviewResponse("User was not found");
            feed.add(response);
            return feed;
        }
        User user = tempUser.get();
        List<String> following = user.getFollowing();

        // Create a new Criteria object and search for reviews with userId in following
        Criteria criteria = new Criteria();
        criteria.and("userId").in(following);

        // Create a new Query object and sort by creation date (newest to oldest)
        Query query = new Query(criteria);
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, "creationDate")));
        List<Review> reviews = mongoTemplate.find(query, Review.class);

        // For each review create an appropriate ReviewResponse and add it to the feed array
        for (Review review : reviews){
            Optional<Album> tempAlbum = albumRepository.findById(review.getMusicId());
            Optional<Song> tempSong = songRepository.findById(review.getMusicId());
            Optional<User> tempCurUser = userRepository.findById(review.getUserId());

            // If user wasn't found then create and add an appropriate ReviewResponse
            if(tempCurUser.isEmpty()){
                ReviewResponse response = new ReviewResponse("User not found");
                feed.add(response);
                continue;
            }

            // If an album and song weren't found then create and add an appropriate ReviewResponse
            if (tempAlbum.isEmpty() && tempSong.isEmpty()){
                ReviewResponse response = new ReviewResponse("Album or song not found");
                feed.add(response);
                continue;
            }

            // Determine if the review was associated with an album or a song and add an appropriate ReviewResponse to feed
            if (tempAlbum.isPresent()){
                ReviewResponse response = new ReviewResponse(review, tempCurUser.get(), tempAlbum.get(), "Review found successfully");
                feed.add(response);
            }
            else {
                ReviewResponse response = new ReviewResponse(review, tempCurUser.get(), tempSong.get(), "Review found successfully");
                feed.add(response);
            }
        }

        return feed;
    }
}