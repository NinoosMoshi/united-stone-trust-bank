package com.ninos.audit_dashboard.service;

import com.ninos.account.dto.AccountDTO;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.transaction.dto.TransactionDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface AuditorService {

    Map<String, Long> getSystemTotal();

    Optional<UserDTO> findUserByEmail(String email);

    Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber);

    List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber);

    Optional<TransactionDTO> findTransactionById(Long transactionId);


}

