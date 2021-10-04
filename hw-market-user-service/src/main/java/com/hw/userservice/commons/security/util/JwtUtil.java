package com.hw.userservice.commons.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtUtil {

  private final String clientSecret;
  private final String issuer;
  private final Long expirationTime;

  public JwtUtil(String clientSecret, String issuer, Long expirationTime) {
    this.clientSecret = clientSecret;
    this.issuer = issuer;
    this.expirationTime = expirationTime;
  }

  public String newToken(Claims claims) {

    LocalDateTime expired = LocalDateTime.now().plus(Duration.ofMillis(expirationTime));

    return Jwts.builder()
        .setClaims(claims)
        .signWith(SignatureAlgorithm.HS512, clientSecret)
        .setIssuer(issuer)
        .setExpiration(toDate(expired))
        .compact();
  }

  public String refreshToken(String token) {

    Claims claims = verify(token);

    LocalDateTime expired = LocalDateTime.now().plus(Duration.ofMillis(expirationTime));

    claims.setExpiration(toDate(expired));

    return newToken(claims);
  }

  public Claims verify(String token) {
    return Jwts.parser().setSigningKey(clientSecret).parseClaimsJws(token).getBody();
  }

  private Date toDate(LocalDateTime time) {
    return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
  }
}
