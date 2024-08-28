package com.project.tempotalk.controllers;

import com.project.tempotalk.models.Artist;
import com.project.tempotalk.payload.request.ArtistRequest;
import com.project.tempotalk.payload.response.ArtistResponse;
import com.project.tempotalk.services.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    @Autowired
    ArtistService artistService;

    // Endpoint for getting all artists in the database
    @GetMapping()
    public ResponseEntity<List<Artist>> getAllArtists(){
        return new ResponseEntity<>(artistService.allArtists(), HttpStatus.OK);
    }

    // Endpoint for getting one artist from the database, based on their ID
    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable String artistId){
        ArtistResponse response = artistService.artistById(artistId);

        if (response.getArtist() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a list of artists from the database, based on their name
    @GetMapping("/username/{name}")
    public ResponseEntity<List<Artist>> getArtistsByName(@PathVariable String name){
        return new ResponseEntity<>(artistService.artistsByName(name), HttpStatus.OK);
    }

    // Endpoint for creating a new artist object in the database
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistResponse> createArtist(@Valid @RequestBody ArtistRequest artistRequest){
        return new ResponseEntity<>(artistService.createArtist(artistRequest), HttpStatus.OK);
    }
}