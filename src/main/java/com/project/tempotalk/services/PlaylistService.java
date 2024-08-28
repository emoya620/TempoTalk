package com.project.tempotalk.services;

import com.project.tempotalk.models.Playlist;
import com.project.tempotalk.models.Song;
import com.project.tempotalk.models.User;
import com.project.tempotalk.payload.request.EditPlaylistRequest;
import com.project.tempotalk.payload.request.PlaylistRequest;
import com.project.tempotalk.payload.response.PlaylistResponse;
import com.project.tempotalk.repositories.PlaylistRepository;
import com.project.tempotalk.repositories.SongRepository;
import com.project.tempotalk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service layer for interacting with Playlists
@Service
public class PlaylistService {

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    UserRepository userRepository;

    // Get all playlists in the database
    public List<Playlist> allPlaylists(){
        return playlistRepository.findAll();
    }

    // Get all playlists associated with a userId
    public Optional<List<Playlist>> getPlaylistsByUserId(String userId){
        return playlistRepository.findPlaylistsByOwnerId(userId);
    }

    // Create a Playlist and assign it to the user who created it
    public PlaylistResponse createPlaylist(PlaylistRequest playlistRequest){
        // Find User who is creating the new playlist
        Optional<User> tempUser = userRepository.findById(playlistRequest.getCreatorId());
        if (tempUser.isEmpty()){
            return new PlaylistResponse("User could not be found");
        }
        User user = tempUser.get();

        // Create Playlist and save it in the repository
        Playlist playlist;
        if (playlistRequest.getDescription() == null){
            playlist = new Playlist(playlistRequest.getName(), playlistRequest.getCreatorId());
        }
        else{
            playlist = new Playlist(playlistRequest.getName(), playlistRequest.getDescription(), playlistRequest.getCreatorId());
        }
        playlistRepository.save(playlist);

        // Update User's playlists
        List<String> userPlaylists = user.getPlaylists();
        userPlaylists.add(playlist.getId());
        user.setPlaylists(userPlaylists);
        userRepository.save(user);

        return new PlaylistResponse(playlist, "Playlist created successfully!");
    }

    // Returns a PlaylistResponse indicating whether a song was successfully added to a playlist
    public PlaylistResponse addSong(EditPlaylistRequest editPlaylistRequest){
        // If song isn't found, then indicate that the song wasn't added to the playlist
        Optional<Song> tempSong = songRepository.findById(editPlaylistRequest.getSongId());
        if (tempSong.isEmpty()){
            return new PlaylistResponse("Song could not be found");
        }

        // If playlist isn't found, then indicate that the song wasn't added to the playlist
        Optional<Playlist> tempPlaylist = playlistRepository.findById(editPlaylistRequest.getPlaylistId());
        if (tempPlaylist.isEmpty()) {
            return new PlaylistResponse("Playlist could not be found");
        }

        // Update playlist to include the newly added song
        Playlist playlist = tempPlaylist.get();
        List<String> playlistTracks = playlist.getTracks();
        playlistTracks.add(editPlaylistRequest.getSongId());
        playlist.setTracks(playlistTracks);
        playlistRepository.save(playlist);

        return new PlaylistResponse(playlist,"Song added successfully!");
    }

    // Returns a PlaylistResponse indicating whether a song was successfully removed to a playlist
    public PlaylistResponse removeSong(EditPlaylistRequest editPlaylistRequest){
        // Indicate if the song being removed wasn't found
        Optional<Song> tempSong = songRepository.findById(editPlaylistRequest.getSongId());
        if (tempSong.isEmpty()){
            return new PlaylistResponse("Song could not be found");
        }

        // Indicate if the playlist being updated wasn't found
        Optional<Playlist> tempPlaylist = playlistRepository.findById(editPlaylistRequest.getPlaylistId());
        if (tempPlaylist.isEmpty()){
            return new PlaylistResponse("Playlist could not be found");
        }

        // Update playlist to not include the song being removed
        Playlist playlist = tempPlaylist.get();
        List<String> playlistTracks = playlist.getTracks();
        playlistTracks.remove(editPlaylistRequest.getSongId());
        playlist.setTracks(playlistTracks);
        playlistRepository.save(playlist);

        return new PlaylistResponse(playlist,"Song removed successfully!");
    }
}