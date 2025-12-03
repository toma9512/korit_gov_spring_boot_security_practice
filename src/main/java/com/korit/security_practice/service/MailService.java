package com.korit.security_practice.service;

import com.korit.security_practice.dto.ApiRespDto;
import com.korit.security_practice.dto.SendMailReqDto;
import com.korit.security_practice.dto.VerifyEmailReqDto;
import com.korit.security_practice.entity.User;
import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.entity.Verify;
import com.korit.security_practice.repository.UserRepository;
import com.korit.security_practice.repository.UserRoleRepository;
import com.korit.security_practice.repository.VerifyRepository;
import com.korit.security_practice.security.model.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailService {
    @Autowired
    private VerifyRepository verifyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, Principal principal) {
        if (!sendMailReqDto.getEmail().equals(principal.getEmail())) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }

        Optional<User> foundUser = userRepository.getUserByUserId(principal.getUserId());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }
        User user = foundUser.get();

        Optional<UserRole> optionalUserRole = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRoleId() == 3).findFirst();
        if (optionalUserRole.isEmpty()) {
            return new ApiRespDto<>("failed", "인증이 필요하지 않은 계정입니다.", null);
        }

        Optional<Verify> optionalVerify = verifyRepository.getVerifyByUserId(principal.getUserId());
        if (optionalVerify.isEmpty()) {
            return new ApiRespDto<>("failed", "이미 인증 완료된 계정입니다.", null);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("[인증] 이메일 인증 요청입니다.");
        message.setTo(sendMailReqDto.getEmail());
        message.setText("인증 코드: " + optionalVerify.get().getVerifyCode());
        javaMailSender.send(message);

        return new ApiRespDto<>("success", "이메일 전송 완료", null);
    }

    public ApiRespDto<?> verifyEmail(VerifyEmailReqDto verifyEmailReqDto, Principal principal) {
        Optional<User> foundUser = userRepository.getUserByUserId(principal.getUserId());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "잘못된 접근입니다.", null);
        }
        User user = foundUser.get();

        Optional<UserRole> optionalUserRole = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRoleId() == 3).findFirst();
        if (optionalUserRole.isEmpty()) {
            return new ApiRespDto<>("failed", "인증이 필요하지 않은 계정입니다.", null);
        }

        Optional<Verify> foundVerify = verifyRepository.getVerifyByUserId(principal.getUserId());
        if (foundVerify.isEmpty()) {
            return new ApiRespDto<>("failed", "이미 인증 완료된 계정입니다.", null);
        }

        if (!verifyEmailReqDto.getVerifyCode().equals(foundVerify.get().getVerifyCode())) {
            return new ApiRespDto<>("failed", "인증 코드를 확인해주세요.", null);
        }

        optionalUserRole.get().setRoleId(2);
        userRoleRepository.updateUserRole(optionalUserRole.get());

        verifyRepository.deleteVerifyByUserId(user.getUserId());

        return new ApiRespDto<>("success", "인증 완료", null);
    }
}
