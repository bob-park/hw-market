package com.hw.userservice.controller.user;

import com.hw.core.model.api.response.ApiResult;
import com.hw.userservice.commons.dto.user.RequestUser;
import com.hw.userservice.commons.dto.user.ResponseUser;
import com.hw.userservice.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.hw.core.model.api.response.ApiResult.ok;

@RestController
@RequestMapping("users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResult<ResponseUser> createUser(
      @Valid @RequestBody RequestUser requestUser) {

    return ok(userService.createUser(requestUser));
  }
}
