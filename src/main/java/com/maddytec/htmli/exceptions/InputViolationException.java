package com.maddytec.htmli.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InputViolationException extends RuntimeException {

  public InputViolationException(String message) {
    super(message);
  }
}
