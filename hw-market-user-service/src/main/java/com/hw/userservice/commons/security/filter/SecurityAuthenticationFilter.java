package com.hw.userservice.commons.security.filter;

import com.hw.userservice.commons.security.model.SecurityAuthentication;
import com.hw.userservice.commons.security.model.SecurityAuthenticationToken;
import com.hw.userservice.commons.security.model.SecurityToken;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SecurityAuthenticationFilter extends GenericFilterBean {

  private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

  private static final String AUTH_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

  private final Logger log = LoggerFactory.getLogger(getClass());

  private SecurityToken securityToken;

  public SecurityAuthenticationFilter() {}

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String authorizationToken = obtainAuthorizationToken(request);
      if (authorizationToken != null) {
        try {
          SecurityToken.Claims claims = verify(request, authorizationToken);
          log.debug("Jwt parse result: {}", claims);

          // 만료 10분 전
          if (canRefresh(claims, (6_000 * 10))) {
            String refreshedToken = securityToken.refreshToken(authorizationToken);
            response.setHeader(AUTH_TOKEN_HEADER, refreshedToken);
          }

          String userId = claims.getUserId();
          String name = claims.getName();
          String email = claims.getEmail();

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

  private boolean canRefresh(SecurityToken.Claims claims, long refreshRangeMillis) {
    long exp = claims.exp();
    if (exp > 0) {
      long remain = exp - System.currentTimeMillis();
      return remain < refreshRangeMillis;
    }
    return false;
  }

  private List<GrantedAuthority> obtainAuthorities(SecurityToken.Claims claims) {
    String[] roles = claims.getRoles();
    return roles == null || roles.length == 0
        ? Collections.emptyList()
        : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList());
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

  private SecurityToken.Claims verify(HttpServletRequest request, String token) {
    return securityToken.verify(token);
  }

  @Autowired
  public SecurityAuthenticationFilter securityToken(SecurityToken securityToken) {
    this.securityToken = securityToken;
    return this;
  }
}
