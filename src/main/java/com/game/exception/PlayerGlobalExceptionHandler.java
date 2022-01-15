package com.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PlayerGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleException(PlayerIncorrectDataException exception) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleException(PlayerNotFoundException exception) {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
