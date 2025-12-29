package com.ninos.transaction.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ninos.account.dto.AccountDTO;
import com.ninos.enums.TransactionStatus;
import com.ninos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionDTO {

    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;
    private LocalDateTime transactionDate;

    private String description;
    private TransactionStatus status;

    @JsonBackReference
    private AccountDTO account;

    // for transfer
    private String sourceAccount;
    private String destinationAccount;

}

