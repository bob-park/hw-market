package com.hw.userservice.commons.dto.user;

import com.hw.userservice.commons.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestUser {

  @NotNull(message = "User Id must be provided.")
  @Size(min = 4, message = "User Id must be longer then 4 characters.")
  private String userId;

  @NotNull(message = "Password must be provided.")
  @Size(min = 4, message = "Password must be longer then 4 characters.")
  private String password;

  @NotNull private String name;

  private String phone;

  @Email private String email;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public RequestUser encryptPassword(String encode) {
    setPassword(encode);
    return this;
  }

  // to User entity
  public User toEntity() {
    return User.builder()
        .userId(getUserId())
        .name(getName())
        .password(getPassword())
        .phone(getPhone())
        .email(getEmail())
        .build();
  }
}
