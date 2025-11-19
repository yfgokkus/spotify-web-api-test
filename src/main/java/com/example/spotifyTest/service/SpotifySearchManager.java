package com.example.spotifyTest.service;

import com.example.spotifyTest.core.util.SpotifyMapper;
import com.example.spotifyTest.model.spotify.ContentType;
import com.example.spotifyTest.model.spotify.PagedSpotifyContent;
import com.example.spotifyTest.model.spotify.ShallowSearchDto;
import com.example.spotifyTest.model.spotify.SpotifyContentDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SpotifySearchManager {

    private final SpotifyClient spotifyClient;
    private final SpotifyMapper spotifyMapper;
    private final ObjectMapper objectMapper;

    public SpotifySearchManager(SpotifyClient spotifyClient, SpotifyMapper spotifyMapper, ObjectMapper objectMapper) {
        this.spotifyClient = spotifyClient;
        this.spotifyMapper = spotifyMapper;
        this.objectMapper = objectMapper;
    }

    public PagedSpotifyContent<? extends SpotifyContentDto> search(String q, ContentType type, int offset, int limit) {
        if (type == ContentType.PLAYLIST) {
            JsonNode playlists = compensatedPlaylistSearch(q, offset, limit);
            return spotifyMapper.toPagedSpotifyContent(ContentType.PLAYLIST, playlists);
        }

        JsonNode result = spotifyClient.search(q, type.value(), offset, limit);

        return spotifyMapper.toPagedSpotifyContent(type, result);
    }

    public ShallowSearchDto shallowSearch(String q, int limit) {
        // Default to all content types if empty
        JsonNode playlistNode;
        JsonNode others;

        Set<String> types = ContentType.valuesSet();

        Set<String> nonPlaylistTypes = types.stream()
                .filter(t -> !"playlist".equals(t))
                .collect(Collectors.toSet());

        CompletableFuture<JsonNode> playlistsFuture = CompletableFuture.supplyAsync(
                () -> compensatedPlaylistSearch(q, 0, limit)
        );

        CompletableFuture<JsonNode> othersFuture = CompletableFuture.supplyAsync(
                () -> {
                    String queryTypes = String.join(",", nonPlaylistTypes);
                    return spotifyClient.search(q, queryTypes, 0, limit);
                }
        );

        try {
            playlistNode = playlistsFuture.get();
            others = othersFuture.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Async multi-type playlist search interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Async multi-type playlist search failed", e.getCause());
        }

        return new ShallowSearchDto(
                spotifyMapper.toTrackDtoList(others),
                spotifyMapper.toAlbumDtoList(others),
                spotifyMapper.toArtistDtoList(others),
                spotifyMapper.toPlaylistDtoList(playlistNode)
        );
    }

    private JsonNode compensatedPlaylistSearch(String query, int offset, int desiredLimit) {
        int batchSize = desiredLimit + 5;
        long total = 0;
        int scanned = 0;

        ArrayNode validItems = objectMapper.createArrayNode();

        while (validItems.size() < desiredLimit) {
            JsonNode response = spotifyClient.search(query, "playlist", offset + scanned, batchSize);
            JsonNode playlistNode = response.path("playlists");
            JsonNode playlists = playlistNode.path("items");

            if (total == 0) {
                total = playlistNode.path("total").asInt();
            }

            if (playlists.isEmpty()) {
                break;
            }

            for (int i = 0; i < playlists.size(); i++) {
                JsonNode item = playlists.get(i);
                scanned++;

                if (isValidItem(item)) {
                    validItems.add(item);
                    if (validItems.size() == desiredLimit) {
                        break;
                    }
                }
            }

            if (offset + scanned >= total) {
                break;
            }

        }

        int newOffset = offset + scanned;

        return buildResponseJson(query, newOffset, desiredLimit, total, validItems);
    }

    private JsonNode buildResponseJson(String query, int newOffset, int desiredLimit, long total, ArrayNode validItems) {

        ObjectNode patchedContentNode = objectMapper.createObjectNode();
        patchedContentNode.put("href", buildHrefPlaylist(query, newOffset, desiredLimit));
        patchedContentNode.put("limit", desiredLimit);
        patchedContentNode.put("offset", newOffset);
        patchedContentNode.put("total", total);
        patchedContentNode.set("items", validItems);

        ObjectNode root = objectMapper.createObjectNode();
        root.set("playlist" + "s", patchedContentNode);

        return root;
    }

    private boolean isValidItem(JsonNode item) {
        return !item.isNull() && item.hasNonNull("id") && item.hasNonNull("name");
    }

    private String buildHrefPlaylist(String query, int offset, int limit) {
        return String.format("https://api.spotify.com/v1/search?q=%s&type=%s&limit=%d&offset=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), "playlist", limit, offset);
    }
}

