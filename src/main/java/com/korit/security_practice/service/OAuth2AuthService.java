package com.korit.security_practice.service;

import com.korit.security_practice.dto.ApiRespDto;
import com.korit.security_practice.dto.OAuth2MergerReqDto;
import com.korit.security_practice.dto.OAuth2SignupReqDto;
import com.korit.security_practice.entity.OAuth2User;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.entity.Verify;
import com.korit.security_practice.repository.OAuth2UserRepository;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.repository.VerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private VerifyRepository verifyRepository;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> foundUserByEmail = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (foundUserByEmail.isPresent()) {
            return new ApiRespDto<>("failed", "중복된 email", null);
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(oAuth2SignupReqDto.getUsername());
        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "중복된 username", null);
        }

        Optional<User> optionalUser = userRepository.addUser(oAuth2SignupReqDto.toUserEntity(bCryptPasswordEncoder));
        if (optionalUser.isEmpty()) {
            return new ApiRespDto<>("failed", "회원 가입 실패", null);
        }
        User user = optionalUser.get();

        UserRole userRole = UserRole.builder()
                .userId(user.getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);

        String verifyCode = String.valueOf((int) (Math.random()*100000));
        Verify verify = Verify.builder()
                .userId(user.getUserId())
                .verifyCode(verifyCode)
                .build();
        verifyRepository.addVerify(verify);

        oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(user.getUserId()));

        return new ApiRespDto<>("success", "회원 가입 성공", null);
    }

    public ApiRespDto<?> merge(OAuth2MergerReqDto oAuth2MergerReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(oAuth2MergerReqDto.getEmail());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "회원 정보가 잘못되었습니다.", null);
        }

        User user = foundUser.get();
        if (!bCryptPasswordEncoder.matches(oAuth2MergerReqDto.getPassword(), user.getPassword())) {
            return new ApiRespDto<>("failed", "회원 정보가 잘못되었습니다.", null);
        }

        Optional<OAuth2User> foundOAuth2User = oAuth2UserRepository
                .getOAuth2UserByProviderAndProviderUserId(oAuth2MergerReqDto.getProvider(), oAuth2MergerReqDto.getProviderUserId());
        if (foundOAuth2User.isPresent()) {
            return new ApiRespDto<>("failed", "이미 연동된 계정입니다.", null);
        }

        oAuth2UserRepository.addOAuth2User(oAuth2MergerReqDto.toEntity(user.getUserId()));

        return new ApiRespDto<>("success", "연동 성공", null);
    }
}
