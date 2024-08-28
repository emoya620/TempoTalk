package com.project.tempotalk.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// Request object for ArtistController endpoints
@Data
@AllArgsConstructor
public class ArtistRequest {
    private String name;
    private List<String> genres;
}