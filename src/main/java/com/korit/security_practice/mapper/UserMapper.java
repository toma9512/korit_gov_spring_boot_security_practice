package com.korit.security_practice.mapper;

import com.korit.security_practice.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByUserId(Integer userId);
    void addUser(User user);
    int updatePassword(User user);
    int updateUsername(User user);
}
