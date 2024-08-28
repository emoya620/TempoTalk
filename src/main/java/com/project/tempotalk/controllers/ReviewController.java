package com.project.tempotalk.controllers;

import com.project.tempotalk.payload.request.EditReviewRequest;
import com.project.tempotalk.payload.request.ReviewRequest;
import com.project.tempotalk.payload.response.ReviewResponse;
import com.project.tempotalk.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    // Endpoint for getting all reviews in the database
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews(){
        return new ResponseEntity<>(reviewService.allReviews(), HttpStatus.OK);
    }

    // Endpoint for getting a list of reviews in the database, based on their music ID
    @GetMapping("/{musicId}")
    public ResponseEntity<List<ReviewResponse>> getMusicReviews(@PathVariable String musicId){
        return new ResponseEntity<>(reviewService.getReviewsByMusicId(musicId), HttpStatus.OK);
    }

    // Endpoint for getting a list of reviews in the database, based on their user ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable String userId){
        return new ResponseEntity<>(reviewService.getReviewsByUserId(userId), HttpStatus.OK);
    }

    // Endpoint for creating a new review object in the database
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest){
        ReviewResponse response = reviewService.createReview(reviewRequest);

        if (response.getReview() == null && response.getMessage().equals("Error: User has already created a review for this music")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (response.getReview() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for editing a review in the database
    @PutMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> editReview(@Valid @RequestBody EditReviewRequest editReviewRequest){
        ReviewResponse response = reviewService.updateReview(editReviewRequest);

        if (response.getReview() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for deleting a review in the database
    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> deleteReview(@PathVariable String reviewId){
        ReviewResponse response = reviewService.deleteReview(reviewId);

        if (response.getReview() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}