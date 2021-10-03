package com.hw.userservice.service.user.impl;

import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.commons.entity.User;
import com.hw.userservice.repository.user.UserRepository;
import com.hw.userservice.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public ResponseUser createUser(RequestUser requestUser) {

    User user =
        requestUser.encryptPassword(passwordEncoder.encode(requestUser.getPassword())).toEntity();

    log.debug("create user: {}", user);

    userRepository.save(user);

    return ResponseUser.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .build();
  }

  @Override
  public ResponseUser getUserByUserId(String userId) {
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId));

    return ResponseUser.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .build();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user =
        userRepository
            .findByUserId(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

    return new org.springframework.security.core.userdetails.User(
        user.getUserId(), user.getPassword(), true, true, true, true, new ArrayList<>());
  }
}
