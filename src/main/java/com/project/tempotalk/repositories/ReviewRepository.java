package com.project.tempotalk.repositories;

import com.project.tempotalk.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for getting Review from the "reviews" collection
@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    Optional<List<Review>> findReviewsByMusicId(String musicId);
    Optional<List<Review>> findReviewsByUserId(String userId);
}