package com.ninos.auth_users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;


@Data
public class RegistrationRequest {

    @NotBlank(message = "first name is required")
    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private List<String> roles;
}
