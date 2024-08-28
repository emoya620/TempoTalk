package com.project.tempotalk.services;

import com.project.tempotalk.models.Album;
import com.project.tempotalk.payload.request.AlbumRequest;
import com.project.tempotalk.payload.response.AlbumResponse;
import com.project.tempotalk.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Service layer for interacting with Albums
@Service
public class AlbumService {
    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    // Return a list of all albums in albumRepository
    public List<Album> allAlbums(){
        return albumRepository.findAll();
    }

    // Retrieve and return an album based on its ID
    public AlbumResponse albumById(String albumId){
        Optional<Album> tempAlbum = albumRepository.findById(albumId);
        if (tempAlbum.isEmpty()){
            return new AlbumResponse("Error: album was not found");
        }
        return new AlbumResponse(tempAlbum.get(), "Album was found successfully");
    }

    // Return a list of all albums that exist by a title, if there are any
    public List<Album> albumsByTitle(String title){
        List<Album> albums = new ArrayList<>();

        Optional<List<Album>> tempAlbums = albumRepository.findAlbumByTitle(title);
        if (tempAlbums.isEmpty()){
            return albums;
        }
        albums = tempAlbums.get();

        return albums;
    }

    // Randomly select albums from the database
    public List<Album> getRandomAlbums(int numAlbums){
        List<Album> randomAlbums;

        SampleOperation sampleStage = Aggregation.sample(numAlbums);
        Aggregation aggregation = Aggregation.newAggregation(sampleStage);
        AggregationResults<Album> output = mongoTemplate.aggregate(aggregation, "albums", Album.class);
        randomAlbums = output.getMappedResults();

        return randomAlbums;
    }

    // Get the most newly released albums in the database
    public List<Album> getNewAlbums(int numAlbums){
        List<Album> newAlbums;

        Query query = new Query();
        query.limit(numAlbums);
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, "releaseDate")));
        newAlbums = mongoTemplate.find(query, Album.class);

        return newAlbums;
    }

    // Create a new album object and store it in our "albums" collection
    public AlbumResponse createAlbum(AlbumRequest albumRequest){
        Album album = new Album(albumRequest.getTitle(), albumRequest.getArtist(), albumRequest.getReleaseDate(), albumRequest.getGenres());
        albumRepository.save(album);
        return new AlbumResponse(album,"Album created successfully!");
    }
}