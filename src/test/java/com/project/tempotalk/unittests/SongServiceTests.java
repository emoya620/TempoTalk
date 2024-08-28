package com.project.tempotalk.unittests;

import com.project.tempotalk.models.Song;
import com.project.tempotalk.payload.request.SongRequest;
import com.project.tempotalk.payload.response.SongResponse;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.services.SongService;
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

// Unit tests for the SongService class
@ExtendWith(MockitoExtension.class)
public class SongServiceTests {
    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song song;
    private List<Song> songs;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        song = new Song("title", "artist", "releaseDate", new ArrayList<>());
        songs = new ArrayList<>();
        songs.add(song);
    }

    // Tests for when a song is not found by an ID
    @Test
    public void SongService_SongById_NotFound(){
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        SongResponse response = songService.songById("songId");
        assertThat(response.getMessage()).isEqualTo("Error: Song was not found");
        assertThat(response.getSong()).isNull();
    }

    // Tests for when a song is found successfully by an ID
    @Test
    public void SongService_SongById_FoundSuccessfully(){
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        SongResponse response = songService.songById("songId");
        assertThat(response.getMessage()).isEqualTo("Song was found successfully");
        assertThat(response.getSong()).isNotNull();
    }

    // Tests for when a song is not found by a title
    @Test
    public void SongService_SongsByTitle_NotFound(){
        when(songRepository.findSongsByTitle(Mockito.anyString())).thenReturn(Optional.empty());
        List<Song> response = songService.songsByTitle("title");
        assertThat(response).isEmpty();
    }

    // Tests for when a song is successfully found by a title
    @Test
    public void SongService_SongsByTitle_FoundSuccessfully(){
        when(songRepository.findSongsByTitle(Mockito.anyString())).thenReturn(Optional.of(songs));
        List<Song> response = songService.songsByTitle("title");
        assertThat(response).isNotEmpty();

    }

    // Tests for when a song is created successfully
    @Test
    public void SongService_CreateSong_Success(){
        SongRequest request = new SongRequest("title","artist","releaseDate", new ArrayList<>());
        when(songRepository.save(Mockito.any(Song.class))).thenReturn(song);
        SongResponse response = songService.createSong(request);
        assertThat(response.getMessage()).isEqualTo("Song created successfully!");
        assertThat(response.getSong()).isNotNull();
    }
}