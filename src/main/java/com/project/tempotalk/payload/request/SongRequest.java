package com.project.tempotalk.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// Request object for SongController endpoints
@Data
@AllArgsConstructor
public class SongRequest {
    private String title;
    private String artist;
    private String releaseDate;
    private List<String> genres;
}