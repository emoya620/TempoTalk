package com.project.tempotalk.payload.response;

import com.project.tempotalk.models.Playlist;
import lombok.AllArgsConstructor;
import lombok.Data;

// Response object for PlaylistController endpoints
@Data
@AllArgsConstructor
public class PlaylistResponse {
    private Playlist playlist = null;
    private String message;

    public PlaylistResponse(String message){
        this.message = message;
    }
}