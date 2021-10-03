package com.hw.apigatewayservice.commons.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public GlobalFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      // Custom Pre filter
      ServerHttpRequest request = exchange.getRequest();
      ServerHttpResponse response = exchange.getResponse();

      log.debug("Global Filter start: request id -> {}", request.getId());

      // Custom Post filter
      return chain
          .filter(exchange)
          .then(
              Mono.fromRunnable(
                  () ->
                      log.debug(
                          "Global Filter end: response code -> {}", response.getStatusCode())));
    };
  }

  public static class Config {}
}
