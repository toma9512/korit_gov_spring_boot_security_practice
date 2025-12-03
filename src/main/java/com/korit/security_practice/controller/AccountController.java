package com.korit.security_practice.controller;

import com.korit.security_practice.dto.UpdatePasswordReqDto;
import com.korit.security_practice.dto.UpdateUsernameReqDto;
import com.korit.security_practice.dto.VerifyCodeReqDto;
import com.korit.security_practice.security.model.Principal;
import com.korit.security_practice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyCodeReqDto verifyCodeReqDto, @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(accountService.verifyEmail(verifyCodeReqDto, principal));
    }

    @PostMapping("/update/password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordReqDto updatePasswordReqDto, @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(accountService.updatePassword(updatePasswordReqDto, principal));
    }

    @PostMapping("/update/email")
    public ResponseEntity<?> updateUsername(@RequestBody UpdateUsernameReqDto updateUsernameReqDto, @AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(accountService.updateUsername(updateUsernameReqDto, principal));
    }
}