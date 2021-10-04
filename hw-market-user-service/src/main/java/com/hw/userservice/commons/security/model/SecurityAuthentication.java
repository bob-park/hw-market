package com.hw.userservice.commons.security.model;

import com.hw.core.model.commons.Id;
import com.hw.userservice.commons.entity.Role;
import com.hw.userservice.commons.entity.User;

public class SecurityAuthentication {

  private final Id<User, Long> id;
  private final String userId;
  private final String name;
  private final String email;

  public SecurityAuthentication(Id<User, Long> id, String userId, String name, String email) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.email = email;
  }

  public Id<User, Long> getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }
}
