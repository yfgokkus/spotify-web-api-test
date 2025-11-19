package com.example.spotifyTest.advice;

import com.example.spotifyTest.controller.SpotifyController;
import com.example.spotifyTest.exception.*;
import com.example.spotifyTest.model.global.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        assignableTypes = { SpotifyController.class }
)
@Slf4j
public class SpotifyExceptionHandler {

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoValidTypeFound(InvalidContentTypeException ex) {
        log.error(ex.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(InvalidSpotifyUriException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoValidTypeFound(InvalidSpotifyUriException ex) {
        log.error(ex.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPlaylistSizeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPlaylistSize(InvalidPlaylistSizeException ex) {
        log.error(ex.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(InvalidSpotifyContentException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSpotifyContent(InvalidSpotifyContentException ex) {
        log.error(ex.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(TypeMismatchException ex) {
        log.error(ex.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
    }
}
