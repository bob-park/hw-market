package com.hw.userservice.service.user;

import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;

public interface UserService {

  ResponseUser createUser(RequestUser requestUser);

  ResponseUser login(String userId, String password);

  ResponseUser getUserByUserId(String userId);

  ResponseUser getById(Long id);
}
