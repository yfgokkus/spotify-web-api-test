package com.example.spotifyTest.model.spotify;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image {
    private String url;
    private int height;
    private int width;
}
