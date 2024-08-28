package com.project.tempotalk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

// Define our Review model and map it to the "reviews" collection in our database
@Document(collection="reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    private String id;
    private String body;
    private int score;
    private String userId;
    private String musicId;
    @CreatedDate
    private Date creationDate;

    public Review(String body, int score, String userId, String musicId){
        this.body = body;
        this.score = score;
        this.userId = userId;
        this.musicId = musicId;
        this.creationDate = new Date();
    }
}