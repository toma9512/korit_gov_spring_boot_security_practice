package com.korit.security_practice.dto;

import com.korit.security_practice.entity.OAuth2User;
import com.korit.security_practice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class OAuth2SignupReqDto {
    private String email;
    private String password;
    private String username;
    private String provider;
    private String providerUserId;

    public User toUserEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .build();
    }

    public OAuth2User toOAuth2UserEntity(Integer userId) {
        return OAuth2User.builder()
                .userId(userId)
                .provider(provider)
                .providerUserId(providerUserId)
                .build();
    }
}
