package com.project.tempotalk.controllers;

import com.project.tempotalk.models.Album;
import com.project.tempotalk.payload.request.AlbumRequest;
import com.project.tempotalk.payload.response.AlbumResponse;
import com.project.tempotalk.services.AlbumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
    AlbumService albumService;

    // Endpoint for getting all albums in the database
    @GetMapping()
    public ResponseEntity<List<Album>> getAllAlbums(){
        return new ResponseEntity<>(albumService.allAlbums(), HttpStatus.OK);
    }

    // Endpoint for getting one album from the database by their ID
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable String albumId){
        AlbumResponse response = albumService.albumById(albumId);

        if (response.getAlbum() == null){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint for getting a list of albums from the database by their name
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Album>> getAlbumsByName(@PathVariable String title){
        return new ResponseEntity<>(albumService.albumsByTitle(title), HttpStatus.OK);
    }

    // Endpoint for getting discovery albums
    @GetMapping("/discovery")
    public ResponseEntity<List<Album>> getDiscoveryAlbums(){
        return new ResponseEntity<>(albumService.getRandomAlbums(21), HttpStatus.OK);
    }

    // Endpoint for getting the most recently released albums in the database
    @GetMapping("/newReleases")
    public ResponseEntity<List<Album>> getNewReleases(){
        return new ResponseEntity<>(albumService.getNewAlbums(21), HttpStatus.OK);
    }

    // Endpoint for creating a new album object in the database
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponse> createAlbum(@Valid @RequestBody AlbumRequest albumRequest){
        return new ResponseEntity<>(albumService.createAlbum(albumRequest), HttpStatus.OK);
    }
}