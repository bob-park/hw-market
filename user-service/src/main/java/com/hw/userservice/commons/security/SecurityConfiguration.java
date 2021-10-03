package com.hw.userservice.commons.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Environment env;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public SecurityConfiguration(Environment env, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.env = env;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.authorizeRequests()
        .antMatchers("/actuator/**") // actuator 적용 제외
        .permitAll()
        .antMatchers("/**")
        .permitAll();
    //        .hasIpAddress(env.getProperty("gateway.ip")) // 해당 IP 가 들어있는 경우만 처리
    //        .and(); // 필터 추가

    // h2-console 을 이용할 경우 반드시 적용해야함
    // frame 으로 구성되어있는 경우 인증때문에 정상적으로 동작안함
    http.headers().frameOptions().disable();
  }
}
