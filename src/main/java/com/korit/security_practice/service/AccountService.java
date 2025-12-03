package com.korit.security_practice.service;

import com.korit.security_practice.dto.ApiRespDto;
import com.korit.security_practice.dto.UpdatePasswordReqDto;
import com.korit.security_practice.dto.UpdateUsernameReqDto;
import com.korit.security_practice.dto.VerifyCodeReqDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.entity.Verify;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.repository.VerifyRepository;
import com.korit.security_practice.security.model.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private VerifyRepository verifyRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApiRespDto<?> verifyEmail(VerifyCodeReqDto verifyCodeReqDto, Principal principal) {
        if (!verifyCodeReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }
        Optional<Verify> foundVerify = verifyRepository.getVerifyByUserId(verifyCodeReqDto.getUserId());

        if (foundVerify.isEmpty()) {
            return new ApiRespDto<>("failed", "이미 인증된 계정입니다.", null );
        }

        if (!verifyCodeReqDto.getVerifyCode().equals(foundVerify.get().getVerifyCode())) {
            return new ApiRespDto<>("failed", "코드가 잘못되었습니다.", null);
        }

        int result = verifyRepository.deleteVerifyByUserId(verifyCodeReqDto.getUserId());
        if (result != 1) {
            return new ApiRespDto<>("failed", "인증 실패", null);
        }

        UserRole userRole = UserRole.builder()
                .userId(verifyCodeReqDto.getUserId())
                .roleId(2)
                .build();

        userRoleRepository.updateUserRole(userRole);

        return new ApiRespDto<>("success", "인증 완료", null);
    }

    public ApiRespDto<?> updatePassword(UpdatePasswordReqDto updatePasswordReqDto, Principal principal) {
        if (!updatePasswordReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다", null);
        }

        Optional<User> foundUser = userRepository.getUserByUserId(updatePasswordReqDto.getUserId());

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보가 잘못되었습니다", null);
        }

        if (!bCryptPasswordEncoder.matches(updatePasswordReqDto.getOldPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "사용자 정보가 잘못되었습니다.", null);
        }

        if (bCryptPasswordEncoder.matches(updatePasswordReqDto.getNewPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "새 비밀번호와 현재 비밀번호가 일치합니다.", null);
        }

        userRepository.updatePassword(updatePasswordReqDto.toEntity(bCryptPasswordEncoder));
        return new ApiRespDto<>("success", "비밀번호 변경 성공", null);
    }

    public ApiRespDto<?> updateUsername(UpdateUsernameReqDto updateUsernameReqDto, Principal principal) {
        if (!updateUsernameReqDto.getUserId().equals(principal.getUserId())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다", null);
        }

        Optional<User> foundUserByUserId = userRepository.getUserByUserId(updateUsernameReqDto.getUserId());

        if (foundUserByUserId.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보가 잘못되었습니다", null);
        }

        if (!bCryptPasswordEncoder.matches(updateUsernameReqDto.getPassword(), foundUserByUserId.get().getPassword())) {
            return new ApiRespDto<>("failed", "사용자 정보가 잘못되었습니다.", null);
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(updateUsernameReqDto.getUsername());

        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "username이 중복되었습니다.", null);
        }

        userRepository.updateUsername(updateUsernameReqDto.toEntity());
        return new ApiRespDto<>("success", "이메일 변경 성공", null);
    }
}
