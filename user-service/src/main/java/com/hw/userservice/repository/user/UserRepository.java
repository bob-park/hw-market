package com.hw.userservice.repository.user;

import com.hw.userservice.commons.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
