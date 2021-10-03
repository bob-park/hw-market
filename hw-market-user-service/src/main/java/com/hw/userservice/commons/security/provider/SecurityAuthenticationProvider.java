package com.hw.userservice.commons.security.provider;

import com.hw.core.exception.NotFoundException;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.commons.entity.Role;
import com.hw.userservice.commons.security.model.AuthenticationRequest;
import com.hw.userservice.commons.security.model.AuthenticationResult;
import com.hw.userservice.commons.security.model.SecurityAuthenticationToken;
import com.hw.userservice.commons.security.model.SecurityToken;
import com.hw.userservice.service.user.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.apache.commons.lang3.ClassUtils.isAssignable;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class SecurityAuthenticationProvider implements AuthenticationProvider {

  private final SecurityToken securityToken;
  private final UserService userService;

  public SecurityAuthenticationProvider(SecurityToken securityToken, UserService userService) {
    this.securityToken = securityToken;
    this.userService = userService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    SecurityAuthenticationToken authenticationToken = (SecurityAuthenticationToken) authentication;
    return processUserAuthentication(authenticationToken.authenticationRequest());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return isAssignable(SecurityAuthenticationToken.class, authentication);
  }

  private Authentication processUserAuthentication(AuthenticationRequest request) {
    try {
      ResponseUser user = userService.login(request.getUserId(), request.getPassword());

      SecurityAuthenticationToken authenticated =
          new SecurityAuthenticationToken(
              user.getUserId(), null, createAuthorityList(user.getRole().getValue()));

      String apiToken = newApiToken(securityToken, user, new String[] {user.getRole().getValue()});

      authenticated.setDetails(new AuthenticationResult(apiToken, user));

      return authenticated;
    } catch (NotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage());
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }

  private String newApiToken(SecurityToken token, ResponseUser user, String[] roles) {
    SecurityToken.Claims claims =
        SecurityToken.Claims.of(user.getUserId(), user.getName(), user.getEmail(), roles);
    return token.newToken(claims);
  }
}
