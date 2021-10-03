package com.hw.apigatewayservice.commons.config;

import org.assertj.core.util.Maps;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebFlux
public class CorsGlobalConfiguration implements WebFluxConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").maxAge(3600);
  }

  @Bean
  public CorsConfiguration corsConfiguration(
      RoutePredicateHandlerMapping routePredicateHandlerMapping) {
    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
    Arrays.asList(
            HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST)
        .forEach(corsConfiguration::addAllowedMethod);
    corsConfiguration.addAllowedOrigin("*");

    routePredicateHandlerMapping.setCorsConfigurations(Maps.newHashMap("/**", corsConfiguration));

    return corsConfiguration;
  }
}
