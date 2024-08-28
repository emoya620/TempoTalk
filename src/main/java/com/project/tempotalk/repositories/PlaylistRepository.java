package com.project.tempotalk.repositories;

import com.project.tempotalk.models.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for getting Playlists from the "playlists" collection
@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    Optional<List<Playlist>> findPlaylistsByOwnerId(String ownerId);
}