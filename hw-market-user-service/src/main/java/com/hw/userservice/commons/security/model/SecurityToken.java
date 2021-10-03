package com.hw.userservice.commons.security.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class SecurityToken {

  private final String issuer;
  private final String clientSecret;
  private final int expirySeconds;
  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;

  public SecurityToken(String issuer, String clientSecret, int expirySeconds) {
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySeconds = expirySeconds;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = JWT.require(algorithm).withIssuer(issuer).build();
  }

  public String newToken(Claims claims) {
    LocalDateTime now = LocalDateTime.now();

    JWTCreator.Builder builder = JWT.create();

    builder.withIssuer(issuer);
    builder.withIssuedAt(toDate(now));

    if (expirySeconds > 0) {
      builder.withExpiresAt(toDate(now.plusSeconds(expirySeconds)));
    }
    builder.withClaim("userId", claims.userId);
    builder.withClaim("name", claims.name);
    builder.withClaim("email", claims.email);
    builder.withArrayClaim("roles", claims.roles);

    return builder.sign(algorithm);
  }

  public String refreshToken(String token) throws JWTVerificationException {
    Claims claims = verify(token);
    claims.eraseIat();
    claims.eraseExp();
    return newToken(claims);
  }

  public Claims verify(String token) throws JWTVerificationException {
    return new Claims(jwtVerifier.verify(token));
  }

  public String getIssuer() {
    return issuer;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public int getExpirySeconds() {
    return expirySeconds;
  }

  public Algorithm getAlgorithm() {
    return algorithm;
  }

  public JWTVerifier getJwtVerifier() {
    return jwtVerifier;
  }

  private Date toDate(LocalDateTime time) {
    return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static class Claims {
    String userId;
    String name;
    String email;
    String[] roles;
    Date iat;
    Date exp;

    private Claims() {}

    Claims(DecodedJWT decodedJWT) {
      Claim userId = decodedJWT.getClaim("userId");
      if (!userId.isNull()) {
        this.userId = userId.asString();
      }

      Claim name = decodedJWT.getClaim("name");

      if (!name.isNull()) {
        this.name = name.asString();
      }

      Claim email = decodedJWT.getClaim("email");

      if (!email.isNull()) {
        this.email = email.asString();
      }

      Claim roles = decodedJWT.getClaim("roles");

      if (!roles.isNull()) {
        this.roles = roles.asArray(String.class);
      }
      this.iat = decodedJWT.getIssuedAt();
      this.exp = decodedJWT.getExpiresAt();
    }

    public static Claims of(String userId, String name, String email, String[] roles) {
      Claims claims = new Claims();
      claims.userId = userId;
      claims.name = name;
      claims.email = email;
      claims.roles = roles;
      return claims;
    }

    public long iat() {
      return iat != null ? iat.getTime() : -1;
    }

    public long exp() {
      return exp != null ? exp.getTime() : -1;
    }

    public void eraseIat() {
      iat = null;
    }

    public void eraseExp() {
      exp = null;
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

    public String[] getRoles() {
      return roles;
    }

    public Date getIat() {
      return iat;
    }

    public Date getExp() {
      return exp;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
          .append("userId", userId)
          .append("name", name)
          .append("email", email)
          .append("roles", Arrays.toString(roles))
          .append("iat", iat)
          .append("exp", exp)
          .toString();
    }
  }
}
