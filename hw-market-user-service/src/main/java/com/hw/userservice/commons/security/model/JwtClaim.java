package com.hw.userservice.commons.security.model;

import com.hw.userservice.commons.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

public class JwtClaim {

  private final String userId;
  private final String name;
  private final String email;
  private final Role role;

  public JwtClaim(String userId, String name, String email, Role role) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.role = role;
  }

  public Claims toClaims() {
    Claims claims = new DefaultClaims();

    claims.put("userId", userId);
    claims.put("name", name);
    claims.put("email", email);
    claims.put("role", role.getValue());

    claims.setSubject(userId);

    return claims;
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

  public Role getRole() {
    return role;
  }
}
