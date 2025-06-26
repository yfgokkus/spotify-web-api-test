package com.example.spotifyTest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SpotifyClient {

    private final WebClient spotifyClient;
    private final SpotifyAuthService authService;

    public SpotifyClient(WebClient.Builder webClientBuilder, SpotifyAuthService authService) {
        this.spotifyClient = webClientBuilder
                .baseUrl("https://api.spotify.com/v1")
                .build();
        this.authService = authService;
    }

    public JsonNode search(String query, String type, int offset, int limit) {
        String token = authService.getAccessToken();

        return spotifyClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", type)
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .queryParam("limit", 5)
                        .queryParam("market", "US")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
    public JsonNode getAlbumById(String albumId) {
        String token = authService.getAccessToken();

        return spotifyClient.get()
                .uri("/albums/{id}", albumId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
