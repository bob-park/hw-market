package com.hw.userservice.commons.dto.user;

public class RequestLogin {

  private String userId;
  private String password;

  public String getUserId() {
    return userId;
  }

  public RequestLogin userId(String userId) {
    this.userId = userId;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public RequestLogin password(String password) {
    this.password = password;
    return this;
  }
}
