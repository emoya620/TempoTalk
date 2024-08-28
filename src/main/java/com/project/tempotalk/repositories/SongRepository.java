package com.project.tempotalk.repositories;

import com.project.tempotalk.models.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for getting Songs from the "songs" collection
@Repository
public interface SongRepository extends MongoRepository<Song, String> {
    Optional<List<Song>> findSongsByTitle(String title);
}