package com.project.tempotalk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

// Define our Artist model and map it to the "artists" collection in our database
@Document(collection="artists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    @Id
    private String id;
    private String name;
    private List<String> genres = new ArrayList<>();
    private List<String> discography = new ArrayList<>();
    private List<String> singles = new ArrayList<>();
    private String artistImage = "https://tempotalk-images.s3.ap-northeast-2.amazonaws.com/artistImages/defaultProfilePicture.png";

    public Artist(String name, List<String> genres){
        this.name = name;
        this.genres = genres;
    }
}