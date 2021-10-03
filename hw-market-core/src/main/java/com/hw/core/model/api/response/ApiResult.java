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

  public static <T> ApiResult<T> error(String description, String detail) {
    return new ApiResult<>(null, new Error(description, detail));
  }

  public static <T> ApiResult<T> error(Error error) {
    return new ApiResult<>(null, error);
  }

  public static <T> ApiResult<T> error(T body, Throwable throwable) {
    return new ApiResult<>(body, new Error(throwable));
  }

  public T getResult() {
    return result;
  }

  public Error getError() {
    return error;
  }
}
