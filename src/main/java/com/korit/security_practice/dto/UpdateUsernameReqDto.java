package com.korit.security_practice.dto;

import com.korit.security_practice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUsernameReqDto {
    private Integer userId;
    private String password;
    private String username;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .username(username)
                .build();
    }
}
