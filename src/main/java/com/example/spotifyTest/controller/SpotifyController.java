package com.example.spotifyTest.controller;

import com.example.spotifyTest.model.spotify.ContentType;
import com.example.spotifyTest.model.spotify.PagedSpotifyContent;
import com.example.spotifyTest.model.spotify.SpotifyContentDto;
import com.example.spotifyTest.service.SpotifySearchManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final SpotifySearchManager spotifySearchManager;

    public SpotifyController(SpotifySearchManager spotifySearchManager) {
        this.spotifySearchManager = spotifySearchManager;
    }

    @GetMapping("/search")
    public ResponseEntity<?> shallowSearch(@RequestParam String q,
                                           @RequestParam(defaultValue = "10") int limit) {
        Object result = spotifySearchManager.shallowSearch(q, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/albums")
    public ResponseEntity<?> searchAlbums(@RequestParam String q,
                                           @RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "10") int limit) {
        Object result = spotifySearchManager.search(q, ContentType.ALBUM, offset, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/tracks")
    public ResponseEntity<?> searchTracks(@RequestParam String q,
                                          @RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "10") int limit) {
        PagedSpotifyContent<? extends SpotifyContentDto> result = spotifySearchManager.search(q, ContentType.TRACK, offset, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/playlists")
    public ResponseEntity<?> searchPlaylists(@RequestParam String q,
                                          @RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "10") int limit) {
        Object result = spotifySearchManager.search(q, ContentType.PLAYLIST, offset, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/artists")
    public ResponseEntity<?> searchArtists(@RequestParam String q,
                                          @RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "10") int limit) {
        Object result = spotifySearchManager.search(q, ContentType.ARTIST, offset, limit);
        return ResponseEntity.ok(result);
    }

}
