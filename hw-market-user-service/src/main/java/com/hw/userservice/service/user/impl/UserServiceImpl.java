package com.hw.userservice.service.user.impl;

import com.hw.core.exception.NotFoundException;
import com.hw.core.model.commons.Id;
import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.commons.entity.User;
import com.hw.userservice.repository.user.UserRepository;
import com.hw.userservice.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    return toUserDto(user);
  }

  @Override
  public ResponseUser getById(Id<User, Long> id) {
    User user =
        userRepository
            .findById(id.getValue())
            .orElseThrow(() -> new NotFoundException(User.class, id.getValue()));

    return toUserDto(user);
  }

  @Override
  public ResponseUser getUserByUserId(String userId) {
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId));

    return toUserDto(user);
  }

  @Override
  public ResponseUser login(String userId, String password) {

    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IllegalArgumentException("Not correct password.");
    }

    return toUserDto(user);
  }

  private ResponseUser toUserDto(User user) {

    return ResponseUser.builder()
        .id(user.getId())
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .role(user.getRole())
        .build();
  }
}
