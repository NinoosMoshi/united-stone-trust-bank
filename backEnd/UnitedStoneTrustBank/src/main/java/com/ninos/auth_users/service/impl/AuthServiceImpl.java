package com.ninos.auth_users.service.impl;

import com.ninos.account.entity.Account;
import com.ninos.auth_users.dto.LoginRequest;
import com.ninos.auth_users.dto.LoginResponse;
import com.ninos.auth_users.dto.RegistrationRequest;
import com.ninos.auth_users.dto.ResetPasswordRequest;
import com.ninos.auth_users.entity.PasswordResetCode;
import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.repository.PasswordResetCodeRepository;
import com.ninos.auth_users.repository.UserRepository;
import com.ninos.auth_users.service.AuthService;
import com.ninos.enums.AccountType;
import com.ninos.enums.Currency;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import com.ninos.response.Response;
import com.ninos.role.entity.Role;
import com.ninos.role.repository.RoleRepository;
import com.ninos.security.TokenService;
import com.ninos.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${password.reset.link}")
    private String resetLink;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;
//    private final AccountService accountService;

    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepository passwordResetCodeRepository;


    @Transactional
    @Override
    public Response<String> register(RegistrationRequest request) {

        List<Role> roles;

        if(request.getRoles() == null || request.getRoles().isEmpty()){
            // Default to CUSTOMER
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("CUSTOMER ROLE NOT FOUND"));

            roles = Collections.singletonList(defaultRole); // or roles = List.of(defaultRole);
        }else {
            roles = request.getRoles()
                    .stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("ROLE NOT FOUND " + roleName))
                    ).toList();
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email already present");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        // CREATE AN ACCOUNT NUMBER FOR THE USER
//        Account savedAccount = accountService.createAccount(AccountType.CHECKING, savedUser);

        // SEND WELCOME EMAIL
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", savedUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome To The Trust Bank 🎉")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO, savedUser);

        // SEND ACCOUNT CREATION/DETAILS EMAIL
        Map<String, Object> accountVars = new HashMap<>();
        accountVars.put("name", savedUser.getFirstName());
//        accountVars.put("accountNumber", savedAccount.getAccountNumber());
        accountVars.put("accountType", AccountType.CHECKING.name());
        accountVars.put("currency", Currency.USD);

        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your New Bank Account Has Been Created ✅")
                .templateName("account-created")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreatedEmail, savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Your account has been created successfully")
//                .data("Email of your account details has been sent to you, Your account number is: " + savedAccount.getAccountNumber())
                .build();
    }



    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Not Found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Password Not Matched");
        }

        String token = tokenService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .token(token)
                .build();

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(loginResponse)
                .build();
    }


    @Transactional
    @Override
    public Response<?> forgetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email Not Found"));

        passwordResetCodeRepository.deleteByUserId(user.getId()); // delete all the codes that user have it

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetCodeRepository.save(resetCode);

        // send email reset link out
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("resetLink", resetLink + code);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notificationDTO,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();
    }


    @Transactional
    @Override
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

        // Find and validate code(check if the code still there)
        PasswordResetCode resetCode = passwordResetCodeRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

        // check expiration
        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())){
            passwordResetCodeRepository.delete(resetCode); // clean up expired code
            throw new BadRequestException("Rest code has expired");
        }

        // Update user password
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the code immediately after successful use
        passwordResetCodeRepository.delete(resetCode);

        // Send confirmation email
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-update-confirmation")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notificationDTO,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password Updated Successfully")
                .build();
    }


    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5); // expired after 5 hours
    }

}

