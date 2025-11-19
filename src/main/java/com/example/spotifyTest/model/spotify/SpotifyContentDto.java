package com.example.spotifyTest.model.spotify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpotifyContentDto {
    private String name;
    private String type;
    private String uri;
    private String url;
    private List<Image> images;
    private boolean failed;
    private String message;

    public static SpotifyContentDto failed(String type, String uri, String message){
        return new SpotifyContentDto(type, uri, message);
    }

    // --- CONSTRUCTOR FOR FAILED FETCHES --- //
    private SpotifyContentDto(String type, String uri, String message) {
        this.type = type;
        this.uri = uri;
        this.failed = true;
        this.message = message;
    }


}

