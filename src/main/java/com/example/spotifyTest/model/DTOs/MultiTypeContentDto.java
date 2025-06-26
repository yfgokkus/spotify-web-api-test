package com.example.spotifyTest.model.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MultiTypeContentDto(
        List<TrackDto> tracks,
        List<AlbumDto> albums,
        List<ArtistDto> artists,
        List<PlaylistDto> playlists
) {}