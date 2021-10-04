package com.hw.userservice.commons.security.config;

import com.hw.userservice.commons.security.filter.SecurityAuthenticationFilter;
import com.hw.userservice.commons.security.handler.EntryPointUnauthorizedHandler;
import com.hw.userservice.commons.security.handler.TokenAccessDeniedHandler;
import com.hw.userservice.commons.security.provider.SecurityAuthenticationProvider;
import com.hw.userservice.commons.security.util.JwtUtil;
import com.hw.userservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.ws.rs.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Environment env;

  private final EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
  private final TokenAccessDeniedHandler accessDeniedHandler;

  public SecurityConfiguration(
      Environment env,
      EntryPointUnauthorizedHandler entryPointUnauthorizedHandler,
      TokenAccessDeniedHandler accessDeniedHandler) {
    this.env = env;
    this.entryPointUnauthorizedHandler = entryPointUnauthorizedHandler;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  @Autowired
  public void configureAuthentication(
      AuthenticationManagerBuilder builder, SecurityAuthenticationProvider authenticationProvider) {
    builder.authenticationProvider(authenticationProvider);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().headers().disable().cors().disable();

    http.exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler)
        .authenticationEntryPoint(entryPointUnauthorizedHandler);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.formLogin().disable();

    http.authorizeRequests()
        .antMatchers("/actuator/**", "/login") // actuator 적용 제외
        .permitAll()
        .antMatchers(HttpMethod.POST, "/users")
        .hasRole("ADMIN")
        //        .permitAll()
        .antMatchers("/users/**")
        .hasAnyRole("ADMIN", "USER")
        .anyRequest()
        .permitAll();

    http.addFilterBefore(
        getSecurityAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public SecurityAuthenticationProvider getSecurityAuthenticationProvider(
      JwtUtil jwtUtil, UserService userService) {
    return new SecurityAuthenticationProvider(jwtUtil, userService);
  }

  @Bean
  public SecurityAuthenticationFilter getSecurityAuthenticationFilter() {
    return new SecurityAuthenticationFilter();
  }
}
