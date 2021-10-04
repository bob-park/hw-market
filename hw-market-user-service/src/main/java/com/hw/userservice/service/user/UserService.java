package com.hw.userservice.service.user;

import com.hw.core.model.commons.Id;
import com.hw.userservice.commons.dto.user.RequestModifyUser;
import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.commons.entity.User;

public interface UserService {

  ResponseUser createUser(RequestUser requestUser);

  ResponseUser login(String userId, String password);

  ResponseUser getUserByUserId(String userId);

  ResponseUser getById(Id<User, Long> id);

  ResponseUser modifyUser(Id<User, Long> id, RequestModifyUser modifyUser);
}
