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
public class TrackDto extends SpotifyContentDto{
    private List<String> artists;
    private String albumType; //album or single
    private String albumName; //nullable
    private String releaseDate; //if from album, album release date
    private long duration;
    private int trackNumber;
}