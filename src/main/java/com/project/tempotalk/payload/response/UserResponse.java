package com.project.tempotalk.payload.response;

import com.project.tempotalk.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// Response object for UserController endpoints
@Data
@AllArgsConstructor
public class UserResponse {
    private User user = null;
    private User followee = null;
    private List<User> following = null;
    private String message;

    public UserResponse(String message){
        this.message = message;
    }

    public UserResponse(User user, String message){
        this.user = user;
        this.message = message;
    }

    public UserResponse(User user, List<User> following, String message){
        this.user = user;
        this.following = following;
        this.message = message;
    }

    public UserResponse(User user, User followee, String message){
        this.user = user;
        this.followee = followee;
        this.message = message;
    }
}