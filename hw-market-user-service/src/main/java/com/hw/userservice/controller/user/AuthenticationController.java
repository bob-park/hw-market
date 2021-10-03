package com.hw.userservice.controller.user;

import com.hw.core.model.api.response.ApiResult;
import com.hw.userservice.commons.exception.UnauthorizedException;
import com.hw.userservice.commons.security.model.AuthenticationRequest;
import com.hw.userservice.commons.security.model.AuthenticationResult;
import com.hw.userservice.commons.security.model.SecurityAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hw.core.model.api.response.ApiResult.ok;

@RestController
@RequestMapping("login")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  public AuthenticationController(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @PostMapping
  public ApiResult<AuthenticationResult> authentication(
      @RequestBody AuthenticationRequest authRequest) throws UnauthorizedException {
    try {
      SecurityAuthenticationToken authToken =
          new SecurityAuthenticationToken(authRequest.getUserId(), authRequest.getPassword());
      Authentication authentication = authenticationManager.authenticate(authToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return ok((AuthenticationResult) authentication.getDetails());
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(e.getMessage());
    }
  }
}
