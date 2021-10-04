package com.hw.apigatewayservice.commons.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.core.model.api.response.ApiResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public class AuthorizationHeaderFilter
    extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Environment env;

  public AuthorizationHeaderFilter(Environment env) {
    super(Config.class);
    this.env = env;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // header token 확인
      if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, "no authorization header.", HttpStatus.UNAUTHORIZED);
      }

      String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

      //            String jwt = authorizationHeader.replace("Bearer", "");
      String jwt = getAuthorizationToken(authorizationHeader);

      if (!isJwtValid(jwt)) {
        return onError(exchange, "JWT token is not valid.", HttpStatus.UNAUTHORIZED);
      }

      return chain.filter(exchange);
    };
  }

  private boolean isJwtValid(String jwt) {

    boolean returnValue = true;

    String subject = null;

    try {
      //      JWTVerifier jwtVerifier =
      //          JWT.require(Algorithm.HMAC512(env.getProperty("token.secret")))
      //              .withIssuer(env.getProperty("token.issuer"))
      //              .build();
      //
      //      DecodedJWT verify = jwtVerifier.verify(jwt);
      //
      //      subject = verify.getPayload();

      Claims claimes =
          Jwts.parser()
              .setSigningKey(env.getProperty("token.secret"))
              .parseClaimsJws(jwt)
              .getBody();

      subject = claimes.getSubject();

    } catch (Exception e) {
      returnValue = false;
    }

    if (subject == null || subject.isEmpty()) {
      returnValue = false;
    }

    return returnValue;
  }

  private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
    ServerHttpResponse response = exchange.getResponse();

    response.setStatusCode(httpStatus);

    log.error(message);

    var error = ApiResult.error(message);

    String body = "";

    try {
      body = new ObjectMapper().writeValueAsString(error); // ! 이게 맞나 싶다.
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage());
    }

    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

    return response.writeWith(Mono.justOrEmpty(buffer));
  }

  private String getAuthorizationToken(String authorizationHeader) {

    String[] parts = authorizationHeader.split(" ");

    if (parts.length == 2) {
      String scheme = parts[0];
      String credentials = parts[1];
      return BEARER.matcher(scheme).matches() ? credentials : null;
    }

    return null;
  }

  public static class Config {}
}
