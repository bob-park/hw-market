package com.hw.userservice.commons.security.filter;

import com.hw.userservice.commons.security.model.SecurityAuthentication;
import com.hw.userservice.commons.security.model.SecurityAuthenticationToken;
import com.hw.userservice.commons.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SecurityAuthenticationFilter extends GenericFilterBean {

  private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

  private static final String AUTH_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

  private final Logger log = LoggerFactory.getLogger(getClass());

  private JwtUtil jwtUtil;

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String authorizationToken = obtainAuthorizationToken(request);
      if (authorizationToken != null) {
        try {
          Claims claims = verify(authorizationToken);
          log.debug("Jwt parse result: {}", claims);

          // 만료 10분 전
          if (canRefresh(claims, (6_000 * 10))) {
            String refreshedToken = jwtUtil.refreshToken(authorizationToken);
            response.setHeader(AUTH_TOKEN_HEADER, refreshedToken);
          }

          String userId = claims.get("userId", String.class);
          String name = claims.get("name", String.class);
          String email = claims.get("email", String.class);

          List<GrantedAuthority> authorities = obtainAuthorities(claims);

          if (nonNull(userId) && isNotEmpty(name) && !authorities.isEmpty()) {

            SecurityAuthenticationToken authentication =
                new SecurityAuthenticationToken(
                    new SecurityAuthentication(userId, name, email), null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (Exception e) {
          log.warn("Jwt processing failed: {}", e.getMessage());
        }
      }
    } else {
      log.debug(
          "SecurityContextHolder not populated with security token, as it already contained: '{}'",
          SecurityContextHolder.getContext().getAuthentication());
    }

    chain.doFilter(request, response);
  }

  private boolean canRefresh(Claims claims, long refreshRangeMillis) {
    long exp = claims.getExpiration().getTime();
    if (exp > 0) {
      long remain = exp - System.currentTimeMillis();
      return remain < refreshRangeMillis;
    }
    return false;
  }

  private List<GrantedAuthority> obtainAuthorities(Claims claims) {
    String role = claims.get("role", String.class);

    return StringUtils.isEmpty(role)
        ? Collections.emptyList()
        : Collections.singletonList(new SimpleGrantedAuthority(role));
  }

  private String obtainAuthorizationToken(HttpServletRequest request) {
    String token = request.getHeader(AUTH_TOKEN_HEADER);
    if (token != null) {

      if (log.isDebugEnabled()) {
        log.debug("Jwt authorization api detected: {}", token);
      }

      try {
        token = URLDecoder.decode(token, StandardCharsets.UTF_8);
        String[] parts = token.split(" ");
        if (parts.length == 2) {
          String scheme = parts[0];
          String credentials = parts[1];
          return BEARER.matcher(scheme).matches() ? credentials : null;
        }
      } catch (IllegalArgumentException e) {
        log.error(e.getMessage(), e);
      }
    }

    return null;
  }

  private Claims verify(String token) {
    return jwtUtil.verify(token);
  }

  @Autowired
  public void setJwtUtil(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }
}
