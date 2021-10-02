package com.hw.userservice.commons.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id @GeneratedValue private Long id;

  private String userId;
  private String password;

  private String name;

  private User(Builder builder) {
    this(null, builder.userId, builder.password, builder.name);
  }

  private User(Long id, String userId, String password, String name) {
    this.id = id;
    this.userId = userId;
    this.password = password;
    this.name = name;
  }

  protected User() {}

  public Long getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

  public static class Builder {

    private String userId;
    private String password;
    private String name;

    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }
}
