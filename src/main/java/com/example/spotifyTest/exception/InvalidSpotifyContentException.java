package com.example.spotifyTest.exception;

public class InvalidSpotifyContentException extends RuntimeException {
    public InvalidSpotifyContentException(String message) {
        super(message);
    }

    public InvalidSpotifyContentException(String message, Throwable cause) {
        super(message,cause);
    }

}
