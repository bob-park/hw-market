package com.hw.userservice.commons.dto.user;

import com.hw.userservice.commons.entity.Role;

public class ResponseUser {

  private final Long id;
  private final String userId;
  private final String name;
  private final String phone;
  private final String email;

  private final Role role;

  private ResponseUser(Builder builder) {
    this(builder.id, builder.userId, builder.name, builder.phone, builder.email, builder.role);
  }

  private ResponseUser(Long id, String userId, String name, String phone, String email, Role role) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.role = role;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getPhone() {
    return phone;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }

  public static class Builder {
    private Long id;
    private String userId;
    private String name;
    private String phone;
    private String email;
    private Role role;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder role(Role role) {
      this.role = role;
      return this;
    }

    public ResponseUser build() {
      return new ResponseUser(this);
    }
  }
}
