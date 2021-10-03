package com.hw.userservice.service.user;

import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

  ResponseUser createUser(RequestUser requestUser);

  ResponseUser login(String userId, String password);

  ResponseUser getUserByUserId(String userId);
}
