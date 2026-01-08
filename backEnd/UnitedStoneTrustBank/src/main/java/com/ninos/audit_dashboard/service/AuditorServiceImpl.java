package com.ninos.audit_dashboard.service;

import com.ninos.account.dto.AccountDTO;
import com.ninos.account.entity.Account;
import com.ninos.account.repository.AccountRepository;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.repository.UserRepository;
import com.ninos.exceptions.NotFoundException;
import com.ninos.transaction.dto.TransactionDTO;
import com.ninos.transaction.entity.Transaction;
import com.ninos.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService{

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;


    @Override
    public Map<String, Long> getSystemTotal() {
        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();

        return Map.of(
                "totalUsers",totalUsers,
                "totalAccounts",totalAccounts,
                "totalTransactions",totalTransactions
        );
    }


    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Email Not Found"));
        return Optional.of(modelMapper.map(user, UserDTO.class));
    }


    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {
//        return accountRepository.findByAccountNumber(accountNumber)
//                .map(account -> modelMapper.map(account, AccountDTO.class));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account number Not Found"));
        return Optional.of(modelMapper.map(account, AccountDTO.class));
    }


    @Override
    public List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactionList = transactionRepository.findByAccount_AccountNumber(accountNumber);
        return transactionList.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();

    }


    @Override
    public Optional<TransactionDTO> findTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Id not found"));
        return Optional.of(modelMapper.map(transaction, TransactionDTO.class));
    }
}

