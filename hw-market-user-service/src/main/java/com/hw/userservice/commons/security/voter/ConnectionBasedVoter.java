package com.hw.userservice.commons.security.voter;

import com.google.common.base.Preconditions;
import com.hw.core.model.commons.Id;
import com.hw.userservice.commons.entity.Role;
import com.hw.userservice.commons.entity.User;
import com.hw.userservice.commons.security.model.SecurityAuthentication;
import com.hw.userservice.commons.security.model.SecurityAuthenticationToken;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.function.Function;

import static org.apache.commons.lang3.ClassUtils.isAssignable;

public class ConnectionBasedVoter implements AccessDecisionVoter<FilterInvocation> {

  private final SimpleGrantedAuthority GRANTE_ADMIN = new SimpleGrantedAuthority(Role.ADMIN.getValue());

  private final RequestMatcher requiresAuthorizationRequestMatcher;

  private final Function<String, Id<User, Long>> idExtractor;

  public ConnectionBasedVoter(
      RequestMatcher requiresAuthorizationRequestMatcher,
      Function<String, Id<User, Long>> idExtractor) {

    Preconditions.checkNotNull(
        requiresAuthorizationRequestMatcher,
        "requiresAuthorizationRequestMatcher must be provided.");
    Preconditions.checkNotNull(idExtractor, "idExtractor must be provided.");

    this.requiresAuthorizationRequestMatcher = requiresAuthorizationRequestMatcher;
    this.idExtractor = idExtractor;
  }

  @Override
  public int vote(
      Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {

    HttpServletRequest request = fi.getRequest();

    if (!requiresAuthorization(request)) {
      return ACCESS_GRANTED;
    }

    if (!isAssignable(SecurityAuthenticationToken.class, authentication.getClass())) {
      return ACCESS_ABSTAIN;
    }

    SecurityAuthentication jwtAuth = (SecurityAuthentication) authentication.getPrincipal();
    Id<User, Long> targetId = obtainTargetId(request);

    // 본인 자신 또는 ADMIN 권한인 경우
    if (jwtAuth.getId().equals(targetId)
        || authentication.getAuthorities().contains(GRANTE_ADMIN)) {
      return ACCESS_GRANTED;
    }

    return ACCESS_DENIED;
  }

  private boolean requiresAuthorization(HttpServletRequest request) {
    return requiresAuthorizationRequestMatcher.matches(request);
  }

  private Id<User, Long> obtainTargetId(HttpServletRequest request) {
    return idExtractor.apply(request.getRequestURI());
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return isAssignable(FilterInvocation.class, clazz);
  }
}
