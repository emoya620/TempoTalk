package com.project.tempotalk.controllers;

import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.FollowRequest;
import com.project.tempotalk.payload.response.ReviewResponse;
import com.project.tempotalk.payload.response.UserResponse;
import com.project.tempotalk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    // Endpoint for getting all users in the database
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(userService.allUsers(), HttpStatus.OK);
    }

    // Endpoint for getting one user in the database, based on their user ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId){
        UserResponse response = userService.userById(userId);

        if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a list of users in the database, based on their username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username){
        UserResponse response = userService.userByUsername(username);

        if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for following a user in the database
    @PutMapping("/follow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> follow(@Valid @RequestBody FollowRequest followRequest){
        UserResponse response = userService.followUser(followRequest);

        if (response.getMessage().equals("Error: User is already being followed")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        else if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for unfollowing a user in the database
    @PutMapping("/unfollow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> unfollow(@Valid @RequestBody FollowRequest followRequest){
        UserResponse response = userService.unfollowUser(followRequest);

        if (response.getMessage().equals("Error: User wasn't being followed")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        else if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a list of the users who are being followed by a given user
    @GetMapping("/following/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getFollowing(@PathVariable String userId){
        UserResponse response = userService.getFollowedUsers(userId);

        if (response.getUser() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a user's feed of their followed users reviews
    @GetMapping("/feed/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getFeed(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserFeed(userId), HttpStatus.OK);
    }
}