package com.example.spotifyTest.model.DTOs;

public record ArtistDto(
        String id,
        String name,
        int popularity,
        String imageUrl,
        String spotifyUrl
) {}