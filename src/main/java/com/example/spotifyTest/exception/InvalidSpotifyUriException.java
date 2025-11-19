package com.example.spotifyTest.exception;

public class InvalidSpotifyUriException extends RuntimeException {
  public InvalidSpotifyUriException(String message) {
    super(message);
  }

  public InvalidSpotifyUriException(String message, Throwable cause) {
    super(message,cause);
  }

}
