package com.ninos.account.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.enums.AccountStatus;
import com.ninos.enums.AccountType;
import com.ninos.enums.Currency;
import com.ninos.transaction.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {


    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;

    @JsonBackReference // this will not be added to the account dto. it will be ignored because it's a back reference
    private UserDTO user;

    private Currency currency;
    private AccountStatus status;

    @JsonManagedReference // it is mean do not return AccountDTO that one inside TransactionDTO
    private List<TransactionDTO> transactions;

    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
