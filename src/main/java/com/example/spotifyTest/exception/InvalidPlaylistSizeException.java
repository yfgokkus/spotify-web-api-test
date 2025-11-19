package com.example.spotifyTest.exception;

public class InvalidPlaylistSizeException extends RuntimeException {
    public InvalidPlaylistSizeException(String message) {
        super(message);
    }

    public InvalidPlaylistSizeException(String message, Throwable cause) {
        super(message,cause);
    }
}
