package com.example.spotifyTest.controller;

import com.example.spotifyTest.model.DTOs.MultiTypeContentDto;
import com.example.spotifyTest.service.SpotifyClient;
import com.example.spotifyTest.core.util.SpotifyMapper;
import com.example.spotifyTest.service.SpotifySearchManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final SpotifySearchManager spotifySearchManager;

    public SpotifyController(SpotifySearchManager spotifySearchManager) {
        this.spotifySearchManager = spotifySearchManager;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchContent(@RequestParam String q,
                                           @RequestParam(required = false) List<String> type,
                                           @RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "10") int limit) {
        Object result = spotifySearchManager.search(q, type, offset, limit);
        return ResponseEntity.ok(result);
    }


//    @GetMapping("/search")
//    public ResponseEntity<JsonNode> searchContent(@RequestParam String q,
//                                                     @RequestParam(required = false) String type) {
//        String searchType = (type != null && !type.isBlank()) ? type : "playlist";
//        JsonNode response = spotifyClient.search(q, searchType);
//        return ResponseEntity.ok(response);
//    }


}
