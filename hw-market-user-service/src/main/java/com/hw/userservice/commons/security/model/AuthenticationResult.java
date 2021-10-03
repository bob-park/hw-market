package com.hw.userservice.commons.security.model;

import com.hw.userservice.commons.dto.user.ResponseUser;

public class AuthenticationResult {

  private final String token;
  private final ResponseUser user;

  public AuthenticationResult(String token, ResponseUser user) {
    this.token = token;
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public ResponseUser getUser() {
    return user;
  }
}
