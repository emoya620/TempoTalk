package com.project.tempotalk.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Service layer for interacting with Reviews
@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    // Return all reviews in reviewRepository
    public List<ReviewResponse> allReviews(){
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAll();

        // For each review, create a new ReviewResponse and add it to the responses list
         for (Review review : reviews){
            Optional<User> tempUser = userRepository.findById(review.getUserId());
            Optional<Album> tempAlbum = albumRepository.findById(review.getMusicId());
            Optional<Song> tempSong = songRepository.findById(review.getMusicId());

            // If the user was not found then return an appropriate ReviewResponse
            if (tempUser.isEmpty()){
                ReviewResponse response = new ReviewResponse("User not found");
                responses.add(response);
                continue;
            }
            User user = tempUser.get();

            // Determine if the music being reviewed is an album or a song and return an appropriate response
            if (tempAlbum.isPresent()){
                Album album = tempAlbum.get();
                ReviewResponse response = new ReviewResponse(review, user, album, "Review found successfully");
                responses.add(response);
            }
            else if (tempSong.isPresent()){
                Song song = tempSong.get();
                ReviewResponse response = new ReviewResponse(review, user, song, "Review found successfully");
                responses.add(response);
            }
        }

        return responses;
    }

    // Return all reviews associated with an album or song
    public List<ReviewResponse> getReviewsByMusicId(String musicId){
        List<ReviewResponse> responses = new ArrayList<>();
        Optional<List<Review>> tempReviews = reviewRepository.findReviewsByMusicId(musicId);

        // If no reviews were found, then return an empty list of responses
        if (tempReviews.isEmpty()){
            return responses;
        }
        List<Review> reviews = tempReviews.get();

        // If no album or songs were found then return an appropriate ReviewResponse
        Optional<Album> tempAlbum = albumRepository.findById(musicId);
        Optional<Song> tempSong = songRepository.findById(musicId);
        Album album = null;
        Song song = null;
        if (tempAlbum.isEmpty() && tempSong.isEmpty()){
            ReviewResponse response = new ReviewResponse("Album or song not found");
            responses.add(response);
            return responses;
        }

        if (tempAlbum.isPresent()){
            album = tempAlbum.get();
        }
        else{
            song = tempSong.get();
        }

        // For all reviews in reviews create an appropriate ReviewResponse and add it to responses
        for (Review review : reviews){
            Optional<User> tempUser = userRepository.findById(review.getUserId());

            // If user was not found then return an appropriate ReviewResponse
            if (tempUser.isEmpty()){
                ReviewResponse response = new ReviewResponse("User not found");
                responses.add(response);
                continue;
            }
            User user = tempUser.get();

            // Determine if the review is associated with an album or a song and return an appropriate response
            if (album != null){
                ReviewResponse response = new ReviewResponse(review, user, album, "Review found successfully");
                responses.add(response);
            }
            else{
                ReviewResponse response = new ReviewResponse(review, user, song, "Review found successfully");
                responses.add(response);
            }
        }

        return responses;
    }

    // Return all reviews associated with a user
    public List<ReviewResponse> getReviewsByUserId(String userId){
        List<ReviewResponse> responses = new ArrayList<>();
        Optional<List<Review>> tempReviews = reviewRepository.findReviewsByUserId(userId);

        // If no reviews were found, then return an empty list of responses
        if (tempReviews.isEmpty()){
            return responses;
        }
        List<Review> reviews = tempReviews.get();

        // If no user was found then return an appropriate ReviewResponse
        Optional<User> tempUser = userRepository.findById(userId);
        if (tempUser.isEmpty()){
            ReviewResponse response = new ReviewResponse("User was not found");
            responses.add(response);
            return responses;
        }
        User user = tempUser.get();

        // For each review add and appropriate ReviewResponse to responses
        for (Review review : reviews){
            Optional<Album> tempAlbum = albumRepository.findById(review.getMusicId());
            Optional<Song> tempSong = songRepository.findById(review.getMusicId());

            // If no album or song was found add an appropriate ReviewResponse to responses
            if (tempAlbum.isEmpty() && tempSong.isEmpty()){
                ReviewResponse response = new ReviewResponse("Album or song not found");
                responses.add(response);
                continue;
            }

            // Determine if the review was associated with an album or a song and create an appropriate ReviewResponse
            if (tempAlbum.isPresent()){
                ReviewResponse response = new ReviewResponse(review, user, tempAlbum.get(), "Review found successfully");
                responses.add(response);
            }
            else {
                ReviewResponse response = new ReviewResponse(review, user, tempSong.get(), "Review found successfully");
                responses.add(response);
            }
        }

        return responses;
    }

    // Create a review and add its ID to a User's and Album/Song's review lists
    public ReviewResponse createReview(ReviewRequest reviewRequest){
        // Check to make sure user specified in the review request exist
        Optional<User> tempUser = userRepository.findById(reviewRequest.getUserId());
        if (tempUser.isEmpty()){
            return new ReviewResponse("Error: User not found");
        }
        User user = tempUser.get();
        List<String> userReviews = user.getReviews();
        List<String> musicReviews;
        Album album = null;
        Song song = null;

        // Make sure that the album/song being reviewed exists
        if (albumRepository.existsById(reviewRequest.getMusicId())){
            Optional<Album> tempAlbum = albumRepository.findById(reviewRequest.getMusicId());
            if (tempAlbum.isEmpty()){
                return new ReviewResponse("Error: Album not found");
            }
            album = tempAlbum.get();
            musicReviews = album.getReviews();
        }
        else if (songRepository.existsById(reviewRequest.getMusicId())){
            Optional<Song> tempSong = songRepository.findById(reviewRequest.getMusicId());
            if (tempSong.isEmpty()){
                return new ReviewResponse("Error: Song not found");
            }
            song = tempSong.get();
            musicReviews = song.getReviews();
        }else{
            return new ReviewResponse("Error: no album or song was found");
        }

        // Check to make sure the user hasn't already created a review for this album/song
        for (String id : userReviews){
            if (musicReviews.contains(id)){
                return new ReviewResponse("Error: User has already created a review for this music");
            }
        }

        // Create a new review object
        Review review = new Review(reviewRequest.getBody(), reviewRequest.getRating(),
                                    reviewRequest.getUserId(), reviewRequest.getMusicId());
        reviewRepository.save(review);

        // Find user who made the review and add the new review ID to their list of reviews
        userReviews.add(review.getId());
        user.setReviews(userReviews);
        userRepository.save(user);

        // Find the album or song associated with the review and add the new review ID to their list of reviews
        if (album != null){
            musicReviews.add(review.getId());
            album.setReviews(musicReviews);

            // Update album score
            List<Integer> scores = new ArrayList<>();
            for (String id : album.getReviews()){
                Optional<Review> r = reviewRepository.findById(id);
                if (r.isPresent()){
                    Review curReview = r.get();
                    scores.add(curReview.getScore());
                }
            }
            album.calculateScore(scores);
            albumRepository.save(album);

            return new ReviewResponse(review, user, album, "Review created successfully!");
        }
        else {
            musicReviews.add(review.getId());
            song.setReviews(musicReviews);

            // Update song score
            List<Integer> scores = new ArrayList<>();
            for (String id : song.getReviews()){
                Optional<Review> r = reviewRepository.findById(id);
                if (r.isPresent()){
                    Review curReview = r.get();
                    scores.add(curReview.getScore());
                }
            }
            song.calculateScore(scores);
            songRepository.save(song);

            return new ReviewResponse(review, user, song, "Review created successfully!");
        }
    }

    // Update a review and update the score of the Album/Song it is associated with
    public ReviewResponse updateReview(EditReviewRequest editReviewRequest){
        // Check to make sure both the review and the album/song specified in the request exist
        if (!reviewRepository.existsById(editReviewRequest.getReviewId())){
            return new ReviewResponse("Error: Review was not found");
        }
        else if (!(albumRepository.existsById(editReviewRequest.getMusicId()) || songRepository.existsById(editReviewRequest.getMusicId()))){
            return new ReviewResponse("Error: Album or song was not found");
        }

        // Find user who made the review and add the new review ID to their list of reviews
        Optional<Review> tempReview = reviewRepository.findById(editReviewRequest.getReviewId());
        if (tempReview.isEmpty()){
            return new ReviewResponse("Error: Review was not found");
        }
        Review review = tempReview.get();

        // If user was not found then return an appropriate ReviewResponse
        Optional<User> tempUser = userRepository.findById(review.getUserId());
        if (tempUser.isEmpty()){
            return new ReviewResponse("Error: User not found");
        }
        User user = tempUser.get();

        review.setBody(editReviewRequest.getBody());
        review.setScore(editReviewRequest.getRating());
        reviewRepository.save(review);

        // Find Album or Song that the updated review was associated with and recalculate its score
        if (albumRepository.existsById(editReviewRequest.getMusicId())){
            Optional<Album> tempAlbum = albumRepository.findById(editReviewRequest.getMusicId());
            if (tempAlbum.isPresent()){
                Album album = tempAlbum.get();

                // Update album score
                List<Integer> scores = new ArrayList<>();
                for (String id : album.getReviews()){
                    Optional<Review> r = reviewRepository.findById(id);
                    if (r.isPresent()){
                        Review curReview = r.get();
                        scores.add(curReview.getScore());
                    }
                }
                album.calculateScore(scores);
                albumRepository.save(album);

                return new ReviewResponse(review, user, album, "Review updated successfully!");
            }
        }
        else {
            Optional<Song> tempSong = songRepository.findById(editReviewRequest.getMusicId());
            if (tempSong.isPresent()){
                Song song = tempSong.get();

                // Update song score
                List<Integer> scores = new ArrayList<>();
                for (String id : song.getReviews()){
                    Optional<Review> r = reviewRepository.findById(id);
                    if (r.isPresent()){
                        Review curReview = r.get();
                        scores.add(curReview.getScore());
                    }
                }
                song.calculateScore(scores);
                songRepository.save(song);

                return new ReviewResponse(review, user, song, "Review updated successfully!");
            }
        }
        return new ReviewResponse("Error: Review was not associated with an album or song");
    }

    // Delete a review, remove its ID from the associated User and Album/Song's reviews list, and recalculate Album/Song score
    public ReviewResponse deleteReview(String reviewId){
        // Delete all traces of the review we are deleting
        Optional<Review> tempReview = reviewRepository.findById(reviewId);
        if (tempReview.isEmpty()){
            return new ReviewResponse("Error: Review was not found");
        }
        Review review = tempReview.get();
        String userId = review.getUserId();
        String musicId = review.getMusicId();

        // Remove the reviewId from associated User's reviews list
        Optional<User> tempUser = userRepository.findById(userId);
        if (tempUser.isEmpty()){
            return new ReviewResponse("Error: Review was not associated with a user");
        }
        User user = tempUser.get();
        List<String> userReviews = user.getReviews();
        userReviews.remove(reviewId);
        user.setReviews(userReviews);
        userRepository.save(user);

        // Remove the reviewId from associated Album/Song's reviews list
        if (albumRepository.existsById(musicId)){
            Optional<Album> tempAlbum = albumRepository.findById(musicId);
            if (tempAlbum.isPresent()){
                Album album = tempAlbum.get();
                List<String> albumReviews = album.getReviews();
                albumReviews.remove(reviewId);
                album.setReviews(albumReviews);

                // Update album score
                List<Integer> scores = new ArrayList<>();
                for (String id : album.getReviews()){
                    Optional<Review> r = reviewRepository.findById(id);
                    if (r.isPresent()){
                        Review curReview = r.get();
                        scores.add(curReview.getScore());
                    }
                }
                album.calculateScore(scores);

                albumRepository.save(album);
                reviewRepository.deleteById(reviewId);
                return new ReviewResponse(review, user, album, "Review deleted successfully!");
            }
        }
        else if (songRepository.existsById(musicId)){
            Optional<Song> tempSong = songRepository.findById(musicId);
            if (tempSong.isPresent()){
                Song song = tempSong.get();
                List<String> songReviews = song.getReviews();
                songReviews.remove(reviewId);
                song.setReviews(songReviews);

                // Update album score
                List<Integer> scores = new ArrayList<>();
                for (String id : song.getReviews()){
                    Optional<Review> r = reviewRepository.findById(id);
                    if (r.isPresent()){
                        Review curReview = r.get();
                        scores.add(curReview.getScore());
                    }
                }
                song.calculateScore(scores);

                songRepository.save(song);

                reviewRepository.deleteById(reviewId);
                return new ReviewResponse(review, user, song, "Review deleted successfully!");
            }
        }
        return new ReviewResponse("Error: Review was not associated with an album or song");
    }
}