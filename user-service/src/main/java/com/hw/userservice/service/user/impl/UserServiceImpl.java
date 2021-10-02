package com.hw.userservice.service.user.impl;

import com.hw.userservice.commons.entity.User;
import com.hw.userservice.repository.user.UserRepository;
import com.hw.userservice.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void createUser() {
    User user = User.builder().userId("test").password("password").name("test1").build();

    log.debug("user: {}", user);

    userRepository.save(user);
  }
}
