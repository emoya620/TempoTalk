package com.project.tempotalk.services;

import com.project.tempotalk.models.Artist;
import com.project.tempotalk.payload.request.ArtistRequest;
import com.project.tempotalk.payload.response.ArtistResponse;
import com.project.tempotalk.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Service layer for interacting with Artists
@Service
public class ArtistService {
    @Autowired
    ArtistRepository artistRepository;

    // Return a list of all artists in the database
    public List<Artist> allArtists(){
        return artistRepository.findAll();
    }

    // Retrieve and return a song based on its ID
    public ArtistResponse artistById(String artistId){
        Optional<Artist> tempArtist = artistRepository.findById(artistId);
        if (tempArtist.isEmpty()){
            return new ArtistResponse("Error: Artist was not found");
        }
        return new ArtistResponse(tempArtist.get(), "Artist was found successfully");
    }

    // Return a list of artists from our database, based on their name
    public List<Artist> artistsByName(String name){
        List<Artist> artists = new ArrayList<>();

        Optional<List<Artist>> tempArtists = artistRepository.findArtistByName(name);
        if (tempArtists.isEmpty()){
            return artists;
        }
        artists = tempArtists.get();

        return artists;
    }

    // Create a new Artist object and store it in our "artists" collection
    public ArtistResponse createArtist(ArtistRequest artistRequest){
        Artist artist = new Artist(artistRequest.getName(), artistRequest.getGenres());
        artistRepository.save(artist);
        return new ArtistResponse(artist,"Artist created successfully!");
    }
}