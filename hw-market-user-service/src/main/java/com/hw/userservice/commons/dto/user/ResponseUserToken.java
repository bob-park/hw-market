package com.hw.userservice.commons.dto.user;

public class ResponseUserToken {

  private final String userId;
  private final String token;

  public ResponseUserToken(String userId, String token) {
    this.userId = userId;
    this.token = token;
  }

  public String getUserId() {
    return userId;
  }

  public String getToken() {
    return token;
  }
}
