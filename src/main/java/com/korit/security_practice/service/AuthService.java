package com.korit.security_practice.service;

import com.korit.security_practice.dto.ApiRespDto;
import com.korit.security_practice.dto.SigninReqDto;
import com.korit.security_practice.dto.SignupReqDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.entity.Verify;
import com.korit.security_practice.jwt.JwtUtils;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.repository.VerifyRepository;
import com.korit.security_practice.security.model.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private VerifyRepository verifyRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> signup(SignupReqDto signupReqDto) {
        Optional<User> foundUserByEmail = userRepository.getUserByEmail(signupReqDto.getEmail());
        if (foundUserByEmail.isPresent()) {
            return new ApiRespDto<>("failed", "중복된 email", signupReqDto.getEmail());
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(signupReqDto.getUsername());
        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "중복된 username", signupReqDto.getUsername());
        }

        Optional<User> optionalUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));

        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);

        String random = String.valueOf(((int) (Math.random()*90000+10000)));
        Verify verify = Verify.builder()
                .userId(optionalUser.get().getUserId())
                .verifyCode(random)
                .build();
        verifyRepository.addVerify(verify);

        return new ApiRespDto<>("success", "회원 가입 성공", optionalUser.get());
    }

    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(signinReqDto.getEmail());

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "회원 정보를 다시 확인하세요", signinReqDto.getEmail());
        }

        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "회원 정보를 다시 확인하세요", signinReqDto.getEmail());
        }

        String token = jwtUtils.generateAccessToken(foundUser.get().getUserId().toString());

        return new ApiRespDto<>("success", "로그인 성공", token);
    }
}
