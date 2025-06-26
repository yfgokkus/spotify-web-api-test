package com.example.spotifyTest.model.DTOs;

public record PlaylistDto(
        String id,
        String name,
        String description,
        String imageUrl,
        String spotifyUrl,
        String owner
) {}