package com.ninos.auth_users.controller;

import com.ninos.auth_users.dto.LoginRequest;
import com.ninos.auth_users.dto.LoginResponse;
import com.ninos.auth_users.dto.RegistrationRequest;
import com.ninos.auth_users.dto.ResetPasswordRequest;
import com.ninos.auth_users.service.AuthService;
import com.ninos.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest request){
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.forgetPassword(resetPasswordRequest.getEmail()));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPasswordRequest));
    }


}

