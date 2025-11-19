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
public class AlbumDto extends SpotifyContentDto{
    private List<String> artists;
    private List<TrackDto> tracks;
    private String releaseDate;
    private int totalTracks;
    private long duration; // derived
}
