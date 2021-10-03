package com.hw.core.model.api.response;

public class Error {

  private final String description;
  private final String detail;

  public Error(Throwable throwable) {
    this(throwable.getMessage(), null);
  }

  public Error(String description, String detail) {
    this.description = description;
    this.detail = detail;
  }

  public String getDescription() {
    return description;
  }

  public String getDetail() {
    return detail;
  }
}
