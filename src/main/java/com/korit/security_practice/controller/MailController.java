package com.korit.security_practice.controller;

import com.korit.security_practice.dto.SendMailReqDto;
import com.korit.security_practice.dto.VerifyEmailReqDto;
import com.korit.security_practice.security.model.Principal;
import com.korit.security_practice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMail(@RequestBody SendMailReqDto sendMailReqDto, @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(mailService.sendMail(sendMailReqDto, principal));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailReqDto verifyEmailReqDto, @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(mailService.verifyEmail(verifyEmailReqDto, principal));
    }
}
