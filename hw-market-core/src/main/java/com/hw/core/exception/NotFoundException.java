package com.hw.core.exception;

public class NotFoundException extends ServiceRuntimeException {

  public NotFoundException() {
    super("Not found.");
  }
}
