package com.hw.userservice.commons.entity;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public enum Role {
  ADMIN("ROLE_ADMIN"),
  USER("ROLE_USER"),
  ;

  private final String value;

  Role(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Role findByValue(String value) {
    return Arrays.stream(Role.values())
        .filter(role -> StringUtils.equalsIgnoreCase(role.getValue(), value))
        .findAny()
        .orElse(null);
  }

  @Override
  public String toString() {
    return getValue();
  }
}
