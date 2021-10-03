package com.hw.userservice.commons.entity;

import com.hw.userservice.commons.entity.base.BaseTimeEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id @GeneratedValue private Long id;

  @Unique
  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  private String phone;

  private String email;

  private User(Builder builder) {
    this(null, builder.userId, builder.password, builder.name, builder.phone, builder.email);
  }

  private User(Long id, String userId, String password, String name, String phone, String email) {
    this.id = id;
    this.userId = userId;
    this.password = password;
    this.name = name;
    this.phone = phone;
    this.email = email;
  }

  protected User() {}

  public static Builder builder() {
    return new Builder();
  }

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

  public String getPhone() {
    return phone;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("id", id)
        .append("userId", userId)
        .append("password", password)
        .append("name", name)
        .append("phone", phone)
        .append("email", email)
        .toString();
  }

  public static class Builder {

    private String userId;
    private String password;
    private String name;

    private String phone;
    private String email;

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

    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }
}
