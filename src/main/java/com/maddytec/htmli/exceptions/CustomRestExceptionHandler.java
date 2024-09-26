package com.maddytec.htmli.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InputViolationException.class)
  public ResponseEntity<ResponseError> handleException(InputViolationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ResponseError.builder().status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage()).build());
  }

  @Getter
  @AllArgsConstructor
  @Builder
  public static class ResponseError {

    private final HttpStatus status;
    private final String message;
  }

}
