package com.example.spotifyTest.model.spotify;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum ContentType {
    ALBUM("album"),
    ARTIST("artist"),
    TRACK("track"),
    PLAYLIST("playlist");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static List<String> valuesList() {
        return Arrays.stream(values())
                .map(ContentType::value)
                .toList();
    }

    public static Set<String> valuesSet() {
        return Arrays.stream(values())
                .map(ContentType::value)
                .collect(Collectors.toSet());
    }

    private static final Map<String, ContentType> BY_VALUE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(ContentType::value, e -> e));

    public static ContentType fromValue(String value) {
        return BY_VALUE.get(value.toLowerCase());
    }

}
