package com.hw.userservice.controller;

import com.hw.core.model.api.response.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hw.core.model.api.response.ApiResult.error;

@RestControllerAdvice
public class RestControllerHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public <T> ApiResult<T> handleNotValid(MethodArgumentNotValidException e) {
    return error(generateBindingMessage(e.getBindingResult()), "");
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public <T> ApiResult<T> handleInternalServiceError(Exception e) {
    return error(e);
  }

  private String generateBindingMessage(BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      String field = bindingResult.getFieldError().getField();

      String defaultMessage = bindingResult.getFieldError().getDefaultMessage();

      return String.format("'%s' %s", field, defaultMessage);
    }

    return null;
  }
}
