package com.korit.security_practice.controller;

import com.korit.security_practice.dto.OAuth2MergerReqDto;
import com.korit.security_practice.dto.OAuth2SignupReqDto;
import com.korit.security_practice.service.OAuth2AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2AuthController {
    @Autowired
    private OAuth2AuthService oAuth2AuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody OAuth2SignupReqDto oAuth2SignupReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.signup(oAuth2SignupReqDto));
    }

    @PostMapping("/merge")
    public ResponseEntity<?> merge(@RequestBody OAuth2MergerReqDto oAuth2MergerReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.merge(oAuth2MergerReqDto));
    }
}
