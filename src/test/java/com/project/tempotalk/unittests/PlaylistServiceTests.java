package com.project.tempotalk.unittests;

import com.project.tempotalk.models.Playlist;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.EditPlaylistRequest;
import com.project.tempotalk.payload.request.PlaylistRequest;
import com.project.tempotalk.payload.response.PlaylistResponse;
import com.project.tempotalk.repositories.PlaylistRepository;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.repositories.UserRepository;
import com.project.tempotalk.services.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Unit tests for the PlaylistService class
@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTests {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private PlaylistService playlistService;

    private Playlist playlist;
    private User user;
    private Song song;

    // Initialize our used variables
    @BeforeEach
    public void init(){
        playlist = new Playlist("name", "description", "ownerId");
        user = new User("name", "email@gmail.com","password");
        song = new Song("title", "artist", "releaseDate",new ArrayList<>());
    }

    // Tests for when a user was not found when creating a new playlist
    @Test
    public void PlaylistService_CreatePlaylist_UserNotFound(){
        PlaylistRequest request = new PlaylistRequest("name","description", "creatorId");
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        PlaylistResponse response = playlistService.createPlaylist(request);
        assertThat(response.getMessage()).isEqualTo("User could not be found");
        assertThat(response.getPlaylist()).isNull();
    }

    // Tests for when a new playlist is created successfully
    @Test
    public void PlaylistService_CreatePlaylist_Success(){
        PlaylistRequest request = new PlaylistRequest("name","description", "creatorId");
        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        when(playlistRepository.save(Mockito.any(Playlist.class))).thenReturn(playlist);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        PlaylistResponse response = playlistService.createPlaylist(request);
        assertThat(response.getMessage()).isEqualTo("Playlist created successfully!");
        assertThat(response.getPlaylist()).isNotNull();
    }

    // Tests for when a song being added to a playlist was not found
    @Test
    public void PlaylistService_AddSong_SongNotFound(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        PlaylistResponse response = playlistService.addSong(request);
        assertThat(response.getMessage()).isEqualTo("Song could not be found");
        assertThat(response.getPlaylist()).isNull();
    }

    // Tests for when a playlist having a song added to was not found
    @Test
    public void PlaylistService_AddSong_PlaylistNotFound(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        when(playlistRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        PlaylistResponse response = playlistService.addSong(request);
        assertThat(response.getMessage()).isEqualTo("Playlist could not be found");
        assertThat(response.getPlaylist()).isNull();
    }

    // Tests for when a song is successfully added to a playlist
    @Test
    public void PlaylistService_AddSong_Success(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        when(playlistRepository.findById(Mockito.anyString())).thenReturn(Optional.of(playlist));
        when(playlistRepository.save(Mockito.any(Playlist.class))).thenReturn(playlist);
        PlaylistResponse response = playlistService.addSong(request);
        assertThat(response.getMessage()).isEqualTo("Song added successfully!");
        assertThat(response.getPlaylist()).isNotNull();
    }

    // Tests for when a song being removed from a playlist was not found
    @Test
    public void PlaylistService_RemoveSong_SongNotFound(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        PlaylistResponse response = playlistService.removeSong(request);
        assertThat(response.getMessage()).isEqualTo("Song could not be found");
        assertThat(response.getPlaylist()).isNull();
    }

    // Tests for when a playlist having a song removed from it was not found
    @Test
    public void PlaylistService_RemoveSong_PlaylistNotFound(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        when(playlistRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        PlaylistResponse response = playlistService.removeSong(request);
        assertThat(response.getMessage()).isEqualTo("Playlist could not be found");
        assertThat(response.getPlaylist()).isNull();
    }

    // Tests for when a song is successfully removed from a playlist
    @Test
    public void PlaylistService_RemoveSong_Success(){
        EditPlaylistRequest request = new EditPlaylistRequest("songId", "playlistId");
        when(songRepository.findById(Mockito.anyString())).thenReturn(Optional.of(song));
        when(playlistRepository.findById(Mockito.anyString())).thenReturn(Optional.of(playlist));
        when(playlistRepository.save(Mockito.any(Playlist.class))).thenReturn(playlist);
        PlaylistResponse response = playlistService.removeSong(request);
        assertThat(response.getMessage()).isEqualTo("Song removed successfully!");
        assertThat(response.getPlaylist()).isNotNull();
    }
}