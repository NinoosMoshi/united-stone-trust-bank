package com.ninos.auth_users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ninos.account.dto.AccountDTO;
import com.ninos.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // if any field is null in DB then the response will ignore that field
@JsonIgnoreProperties(ignoreUnknown = true) // ignore the data that does not have a value
public class UserDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    @JsonIgnore // it will ignore the response(it will not show the response of password)
    private String password;

    private String profilePictureUrl;
    private boolean active;

    private List<Role> roles;

    // mean return the user and account except the user inside account(return just user and account and ignore or do not return the user inside account)
    @JsonManagedReference // When you have two entities referencing each other(User → Account → User → Account → … endlessly). we use this annotation to prevent that
    private List<AccountDTO> accounts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
