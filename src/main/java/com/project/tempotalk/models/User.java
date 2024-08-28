package com.project.tempotalk.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

// Define our User model and map it to the "users" collection in our database
@Document(collection="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @NotBlank
    @Size(max = 25)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;
    @CreatedDate
    private Date creationDate;
    private List<String> following = new ArrayList<>();
    private List<String> reviews = new ArrayList<>();
    private List<String> playlists = new ArrayList<>();
    @DBRef
    private Set<Role> roles = new HashSet<>();
    private String profileImage = "https://tempotalk-images.s3.ap-northeast-2.amazonaws.com/profileImages/defaultProfilePicture.png";

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
        this.creationDate = new Date();
    }
}