package com.hw.userservice.commons.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.core.exception.ServiceRuntimeException;
import com.hw.userservice.commons.dto.user.RequestLogin;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.commons.dto.user.ResponseUserToken;
import com.hw.userservice.service.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Environment env;

  private final UserService userService;

  public AuthenticationFilter(
      Environment env, UserService userService, AuthenticationManager authenticationManager) {
    super(authenticationManager);

    this.env = env;
    this.userService = userService;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      RequestLogin creds =
          new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

      return getAuthenticationManager()
          .authenticate(
              new UsernamePasswordAuthenticationToken(
                  creds.getUserId(), creds.getPassword(), new ArrayList<>()));

    } catch (IOException e) {
      throw new ServiceRuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {

    String username = ((User) authResult.getPrincipal()).getUsername();

    ResponseUser userDetails = userService.getUserByUserId(username);

    LocalDateTime expired =
        LocalDateTime.now()
            .plus(Duration.ofMillis(Long.parseLong(env.getProperty("token.expiration-time"))));

    String token =
        Jwts.builder()
            .setSubject(userDetails.getUserId())
            .setExpiration(Date.from(expired.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
            .compact();

    ResponseUserToken responseUserToken = new ResponseUserToken(username, token);

    response.getOutputStream().print(new ObjectMapper().writeValueAsString(responseUserToken));
    response.flushBuffer();
  }
}
