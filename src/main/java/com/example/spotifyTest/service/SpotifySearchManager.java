package com.example.spotifyTest.service;

import com.example.spotifyTest.core.util.SpotifyMapper;
import com.example.spotifyTest.model.DTOs.MultiTypeContentDto;
import com.example.spotifyTest.model.PagedResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class SpotifySearchManager {

    private final SpotifyClient spotifyClient;
    private final SpotifyMapper mapper;
    private final ObjectMapper objectMapper;

    private final List<String> contentTypes = List.of("track", "album", "artist", "playlist");


    public SpotifySearchManager(SpotifyClient spotifyClient, SpotifyMapper mapper, ObjectMapper objectMapper) {
        this.spotifyClient = spotifyClient;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public Object search(String q, List<String> types, int offset, int limit) {
        List<String> typeList = validateTypes(types);

        boolean isEmpty = typeList == null || typeList.isEmpty();
        boolean isOnlyPlaylist = typeList != null && typeList.size() == 1 && typeList.contains("playlist");
        boolean isMultiType = typeList != null && typeList.size() > 1;

        // Case 1: No types specified — fetch playlists and others asynchronously
        if (isEmpty) {
            return shallowSearch(q, offset, limit);
        }

        // Case 2: Only playlists requested — use compensation and extract real offset/limit
        if (isOnlyPlaylist) {
            return safePlaylistSearch(q, offset, limit);
        }

        // Multi-type (excluding playlist)
        if (isMultiType && !typeList.contains("playlist")) {
            return multiTypeNullSafeSearch(q, typeList, offset, limit);
        }

        return singleTypeNullSafeSearch(q, typeList, offset, limit);
    }

    public Object multiTypeNullSafeSearch(String q, List<String> types , int offset, int limit) {
        // Case 3: Multi-type (without playlist) or single non-playlist type
        String queryTypes = String.join(",", types);
        JsonNode result = spotifyClient.search(q, queryTypes, offset, limit);

        return new MultiTypeContentDto(
                mapper.mapTracks(result),
                mapper.mapAlbums(result),
                mapper.mapArtists(result),
                null // playlists not fetched
        );
    }

    public Object singleTypeNullSafeSearch(String q, List<String> types , int offset, int limit) {
        // Case 3: Multi-type (without playlist) or single non-playlist type
        String queryTypes = String.join(",", types);
        JsonNode result = spotifyClient.search(q, queryTypes, offset, limit);

        // Case 4: Single non-playlist type
        String singleType = types.getFirst();
        int total = result.path(singleType + "s").path("total").asInt();

        return mapper.mapToPagedResponse(singleType, result, offset, limit, total);
    }

    public Object shallowSearch(String q, int offset, int limit) {
        CompletableFuture<JsonNode> playlistFuture = CompletableFuture.supplyAsync(
                () -> compensatedPlaylistSearch(q, offset, limit)
        );

        CompletableFuture<JsonNode> othersFuture = CompletableFuture.supplyAsync(
                () -> spotifyClient.search(q, "track,album,artist", offset, limit)
        );

        CompletableFuture.allOf(playlistFuture, othersFuture).join();

        try {
            JsonNode playlists = playlistFuture.get();
            JsonNode others = othersFuture.get();

            return new MultiTypeContentDto(
                    mapper.mapTracks(others),
                    mapper.mapAlbums(others),
                    mapper.mapArtists(others),
                    mapper.mapPlaylists(playlists)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Async search was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Async search failed", e.getCause());
        }
    }

    private Object safePlaylistSearch(String q, int offset, int limit) {
        JsonNode playlistResult = compensatedPlaylistSearch(q, offset, limit);
        JsonNode playlistsNode = playlistResult.path("playlists");

        int actualOffset = playlistsNode.path("offset").asInt();
        int actualLimit = playlistsNode.path("limit").asInt();
        int total = playlistsNode.path("total").asInt();

        return new PagedResponse<>(mapper.mapPlaylists(playlistResult), actualOffset, actualLimit, total);
    }

    public JsonNode compensatedPlaylistSearch(String query, int offset, int desiredLimit) {
        int batchSize = desiredLimit + 5;
        int actualTotal = 0;
        int consumed = 0;

        ArrayNode validItems = objectMapper.createArrayNode();

        while (validItems.size() < desiredLimit) {
            JsonNode response = spotifyClient.search(query, "playlist", offset + consumed, batchSize);
            JsonNode typeNode = response.path("playlist" + "s"); // e.g. playlists, episodes
            JsonNode items = typeNode.path("items");

            if (actualTotal == 0) {
                actualTotal = typeNode.path("total").asInt();
            }

            if (items.isEmpty()) {
                break;
            }

            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                consumed++;

                if (isValidItem(item)) {
                    validItems.add(item);
                    if (validItems.size() == desiredLimit) {
                        break;
                    }
                }
            }

            if (offset + consumed >= actualTotal) {
                break;
            }
        }

        int nextOffset = offset + consumed;

        ObjectNode patchedContentNode = objectMapper.createObjectNode();
        patchedContentNode.put("href", buildHref(query, "playlist", offset, desiredLimit));
        patchedContentNode.put("limit", desiredLimit);
        patchedContentNode.put("offset", nextOffset);
        patchedContentNode.put("total", actualTotal);
        patchedContentNode.set("items", validItems);

        ObjectNode root = objectMapper.createObjectNode();
        root.set("playlist" + "s", patchedContentNode);

        return root;
    }

    private List<String> validateTypes(List<String> types) {
        if (types == null) return List.of();
        return types.stream()
                .map(String::toLowerCase)
                .filter(contentTypes::contains)
                .toList();
    }

    // Can be enhanced with validation strategy for different types
    private boolean isValidItem(JsonNode item) {
        return !item.isNull() && item.hasNonNull("id") && item.hasNonNull("name");
    }

    private String buildHref(String query, String type, int offset, int limit) {
        return String.format("https://api.spotify.com/v1/search?q=%s&type=%s&limit=%d&offset=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), type, limit, offset);
    }
}

