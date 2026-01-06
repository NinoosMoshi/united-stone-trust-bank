package com.ninos.auth_users.service;

import com.ninos.auth_users.dto.LoginRequest;
import com.ninos.auth_users.dto.LoginResponse;
import com.ninos.auth_users.dto.RegistrationRequest;
import com.ninos.auth_users.dto.ResetPasswordRequest;
import com.ninos.response.Response;


public interface AuthService {

    Response<String> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);

}
