package com.hw.userservice.commons.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.core.model.api.response.ApiResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

  private static final ApiResult<Object> E403 =
      ApiResult.error("Authentication error (cause: forbidden)");

  private final ObjectMapper om;

  public TokenAccessDeniedHandler(ObjectMapper om) {
    this.om = om;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setHeader("content-type", "application/json");
    response.getWriter().write(om.writeValueAsString(E403));
    response.getWriter().flush();
    response.getWriter().close();
  }
}
