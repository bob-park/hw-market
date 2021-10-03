package com.hw.userservice.commons.security.config;

import com.hw.userservice.commons.security.filter.AuthenticationFilter;
import com.hw.userservice.service.user.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Environment env;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final UserService userService;

  public SecurityConfiguration(
      Environment env, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
    this.env = env;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userService = userService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.authorizeRequests()
        .antMatchers("/actuator/**") // actuator 적용 제외
        .permitAll()
        .antMatchers("/**")
        .permitAll()
        .and()
        .addFilter(getAuthenticationFilter());

    //        .hasIpAddress(env.getProperty("gateway.ip")) // 해당 IP 가 들어있는 경우만 처리
    //        .and(); // 필터 추가

    // h2-console 을 이용할 경우 반드시 적용해야함
    // frame 으로 구성되어있는 경우 인증때문에 정상적으로 동작안함
    http.headers().frameOptions().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
  }

  private AuthenticationFilter getAuthenticationFilter() throws Exception {

    return new AuthenticationFilter(env, userService, authenticationManager());
  }
}
