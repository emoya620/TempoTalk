package com.project.tempotalk.repositories;

import com.project.tempotalk.models.Album;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for getting Albums from the "albums" collection
@Repository
public interface AlbumRepository extends MongoRepository<Album, String> {
    Optional<List<Album>> findAlbumByTitle(String title);
}