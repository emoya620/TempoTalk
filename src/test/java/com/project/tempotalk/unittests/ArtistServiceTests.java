package com.project.tempotalk.unittests;

import com.project.tempotalk.models.Artist;
import com.project.tempotalk.payload.request.ArtistRequest;
import com.project.tempotalk.payload.response.ArtistResponse;
import com.project.tempotalk.repositories.ArtistRepository;
import com.project.tempotalk.services.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Unit tests for the ArtistService class
@ExtendWith(MockitoExtension.class)
public class ArtistServiceTests {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private Artist artist;
    private List<Artist> artists;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        artist = new Artist("name", new ArrayList<>());
        artists = new ArrayList<>();
        artists.add(artist);
    }

    // Tests for when an artist is not found by ID
    @Test
    public void ArtistService_ArtistById_NotFound(){
        when(artistRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ArtistResponse response = artistService.artistById("id");
        assertThat(response.getMessage()).isEqualTo("Error: Artist was not found");
        assertThat(response.getArtist()).isNull();
    }

    // Tests for when an artist is found successfully by ID
    @Test
    public void ArtistService_ArtistById_FoundSuccessfully(){
        when(artistRepository.findById(Mockito.anyString())).thenReturn(Optional.of(artist));
        ArtistResponse response = artistService.artistById("id");
        assertThat(response.getMessage()).isEqualTo("Artist was found successfully");
        assertThat(response.getArtist()).isNotNull();
    }

    // Tests for when artists are not found by name
    @Test
    public void ArtistService_ArtistByName_NotFound(){
        when(artistRepository.findArtistByName(Mockito.anyString())).thenReturn(Optional.empty());
        List<Artist> response = artistService.artistsByName("name");
        assertThat(response).isEmpty();
    }

    // Tests for when artists are successfully found by name
    @Test
    public void ArtistService_ArtistByName_FoundSuccessfully(){
        when(artistRepository.findArtistByName(Mockito.anyString())).thenReturn(Optional.of(artists));
        List<Artist> response = artistService.artistsByName("name");
        assertThat(response).isNotEmpty();
    }

    // Tests for when an artist is successfully created
    @Test
    public void ArtistService_CreateArtist_Success(){
        ArtistRequest request = new ArtistRequest("name", new ArrayList<>());
        when(artistRepository.save(Mockito.any(Artist.class))).thenReturn(artist);
        ArtistResponse response = artistService.createArtist(request);
        assertThat(response.getMessage()).isEqualTo("Artist created successfully!");
        assertThat(response.getArtist()).isNotNull();
    }
}