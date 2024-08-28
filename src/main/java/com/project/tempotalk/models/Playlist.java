package com.project.tempotalk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

// Define our Playlist model and map it to the "playlists" collection in our database
@Document(collection="playlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    private String id;
    private String name;
    private String description = "";
    private String ownerId;
    private List<String> tracks = new ArrayList<>();
    private String coverArt = "https://tempotalk-images.s3.ap-northeast-2.amazonaws.com/coverArt/defaultCoverArt.png";

    public Playlist(String name, String ownerId){
        this.name = name;
        this.ownerId = ownerId;
    }

    public Playlist(String name, String description, String ownerId){
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }
}