package com.hw.userservice.commons.audit;

import com.hw.userservice.commons.security.model.SecurityAuthentication;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .map(
            authentication -> {
              SecurityAuthentication auth = (SecurityAuthentication) authentication.getPrincipal();

              return auth.getUserId();
            });
  }
}
