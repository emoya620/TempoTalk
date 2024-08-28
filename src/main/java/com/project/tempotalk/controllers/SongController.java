package com.project.tempotalk.controllers;

import com.project.tempotalk.models.Song;
import com.project.tempotalk.payload.request.SongRequest;
import com.project.tempotalk.payload.response.SongResponse;
import com.project.tempotalk.services.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/songs")
public class SongController {
    @Autowired
    SongService songService;

    // Endpoint for getting all songs in the database
    @GetMapping()
    public ResponseEntity<List<Song>> getAllSongs(){
        return new ResponseEntity<>(songService.allSongs(), HttpStatus.OK);
    }

    // Endpoint for getting one song in the database, based on their song ID
    @GetMapping("/{songId}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable String songId){
        SongResponse response = songService.songById(songId);

        if (response.getSong() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a list of songs from the database, based on their title
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Song>> getSongsByTitle(@PathVariable String title){
        return new ResponseEntity<>(songService.songsByTitle(title), HttpStatus.OK);
    }

    // Endpoint for creating a new song object in the database
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongResponse> createSong(@Valid @RequestBody SongRequest songRequest){
        return new ResponseEntity<>(songService.createSong(songRequest), HttpStatus.OK);
    }
}