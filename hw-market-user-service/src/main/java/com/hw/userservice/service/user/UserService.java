package com.hw.userservice.service.user;

import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    ResponseUser createUser(RequestUser requestUser);

    ResponseUser getUserByUserId(String userId);

}
