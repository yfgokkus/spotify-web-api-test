package com.example.spotifyTest.core.util;

import com.example.spotifyTest.model.spotify.PlaylistDto;
import com.example.spotifyTest.model.spotify.SpotifyContentDto;

//TODO: VALIDATOR IS UNUSED
public class SpotifyContentValidator {
    private static final int MULTI_TRACK_CONTENT_LIMIT = 20;

    private SpotifyContentValidator() {}

    public static SpotifyContentDto validate(SpotifyContentDto content) {

        return switch (content.getType()) {
            case "playlist" -> validatePlaylist(content);
            /*case "album":
                yield content;
            case "track":
                yield content;
            case "artist":
                yield content;*/
            default -> content;
        };
    }

    private static SpotifyContentDto validatePlaylist(SpotifyContentDto content) {
        String type  = content.getType();
        if (content instanceof PlaylistDto contentDto) {
            if (contentDto.getTracks().isEmpty()) {
                return new SpotifyContentDto(type, content.getUri(), "No tracks found");
            }
            if (contentDto.getTracks().size() > MULTI_TRACK_CONTENT_LIMIT) {
                return new SpotifyContentDto(type, content.getUri(), "Invalid playlists size");
            }
        }
        return content;
    }
}
