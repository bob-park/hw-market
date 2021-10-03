package com.hw.userservice.commons.exception;

import com.hw.core.exception.ServiceRuntimeException;

public class UnauthorizedException extends ServiceRuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }
}
