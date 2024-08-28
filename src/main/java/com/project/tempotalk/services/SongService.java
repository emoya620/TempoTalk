package com.project.tempotalk.services;

import com.project.tempotalk.models.Song;
import com.project.tempotalk.payload.request.SongRequest;
import com.project.tempotalk.payload.response.SongResponse;
import com.project.tempotalk.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Service layer for interacting with Songs
@Service
public class SongService {
    @Autowired
    SongRepository songRepository;

    // Return a list of all songs in songRepository
    public List<Song> allSongs(){
        return songRepository.findAll();
    }

    // Retrieve and return a song based on its ID
    public SongResponse songById(String songId){
        Optional<Song> tempSong = songRepository.findById(songId);
        if (tempSong.isEmpty()){
            return new SongResponse("Error: Song was not found");
        }
        return new SongResponse(tempSong.get(), "Song was found successfully");
    }

    // Return a list of all songs that exist by a title, if there are any
    public List<Song> songsByTitle(String title){
        List<Song> songs = new ArrayList<>();

        Optional<List<Song>> tempSongs = songRepository.findSongsByTitle(title);
        if (tempSongs.isEmpty()){
            return songs;
        }
        songs = tempSongs.get();

        return songs;
    }

    // Create a new Song object and store it in our "songs" collection
    public SongResponse createSong(SongRequest songRequest){
        Song song = new Song(songRequest.getTitle(), songRequest.getArtist(), songRequest.getReleaseDate(), songRequest.getGenres());
        songRepository.save(song);
        return new SongResponse(song,"Song created successfully!");
    }
}
