package com.hw.core.model.api.response;

public class ApiResult<T> {

  private final T result;

  private final Error error;

  public ApiResult(T result, Error error) {
    this.result = result;
    this.error = error;
  }

  public static <T> ApiResult<T> ok(T result) {
    return new ApiResult<>(result, null);
  }

  public static <T> ApiResult<T> error(Throwable throwable) {
    return new ApiResult<>(null, new Error(throwable));
  }

  public static <T> ApiResult<T> error(String description) {
    return error(description, null);
  }

  public static <T> ApiResult<T> error(String description, String detail) {
    return new ApiResult<>(null, new Error(description, detail));
  }

  public static <T> ApiResult<T> error(Error error) {
    return new ApiResult<>(null, error);
  }

  public T getResult() {
    return result;
  }

  public Error getError() {
    return error;
  }
}
