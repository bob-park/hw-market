package com.hw.core.exception;

public class ServiceRuntimeException extends RuntimeException {

  public ServiceRuntimeException(String message) {
    super(message);
  }

  public ServiceRuntimeException(Throwable cause) {
    super(cause);
  }
}
