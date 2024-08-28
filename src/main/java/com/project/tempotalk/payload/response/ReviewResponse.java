package com.project.tempotalk.payload.response;

import com.project.tempotalk.models.Album;
import com.project.tempotalk.models.Review;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import lombok.Data;

// Response object for ReviewController endpoints
@Data
public class ReviewResponse {
    private Review review = null;
    private User user = null;
    private Album album = null;
    private Song song = null;
    private String message;

    public ReviewResponse(Review review, User user, Album album, String message){
        this.review = review;
        this.user = user;
        this.album = album;
        this.message = message;
    }

    public ReviewResponse(Review review, User user, Song song, String message){
        this.review = review;
        this.user = user;
        this.song = song;
        this.message = message;
    }

    public ReviewResponse(String message){
        this.message = message;
    }
}