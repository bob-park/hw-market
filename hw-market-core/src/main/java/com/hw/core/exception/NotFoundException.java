package com.hw.core.exception;

import org.apache.commons.lang3.StringUtils;

public class NotFoundException extends ServiceRuntimeException {

  public NotFoundException() {
    super("Not found.");
  }

  public NotFoundException(Class<?> cls, Object... values) {
    this(cls.getSimpleName(), values);
  }

  public NotFoundException(String targetName, Object... values) {
    super(
        String.format(
            "Could not found '%s' with query values '%s", targetName, StringUtils.join(values)));
  }
}
