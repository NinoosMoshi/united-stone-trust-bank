package com.ninos.account.service;

import com.ninos.account.dto.AccountDTO;
import com.ninos.account.entity.Account;
import com.ninos.account.repository.AccountRepository;
import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.service.UserService;
import com.ninos.enums.AccountStatus;
import com.ninos.enums.AccountType;
import com.ninos.enums.Currency;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final Random random = new Random();


    @Override
    public Account createAccount(AccountType accountType, User user) {

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currency(Currency.USD)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }


    @Override
    public Response<List<AccountDTO>> getMyAccounts() {

        User user = userService.getCurrentLoggedInUser();

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        List<AccountDTO> accountDTOS = accounts.stream()
                .map(account -> modelMapper.map(account,AccountDTO.class))
                .toList();

        return Response.<List<AccountDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your accounts were retrieved successfully.")
                .data(accountDTOS)
                .build();
    }


    @Transactional
    @Override
    public Response<?> closeAccount(String accountNumber) {

        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account Not Found"));

        if(!user.getAccounts().contains(account)){
            throw new NotFoundException("Account does not belong to you");
        }
        if(account.getBalance().compareTo(BigDecimal.ZERO) > 0){ // compareTo() => return (-1 less than) or (0 equal) or (1 greater than). BigDecimal.ZERO => 0.00
            throw new BadRequestException("Account balance must be zero before closing");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account close successfully.")
                .build();
    }


    private String generateAccountNumber(){
        String accountNumber;
        do {
            accountNumber = "66" + (random.nextInt(90000000) + 10000000); // return 8 random digits.

        }while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        log.info("account number is generated {}", accountNumber);
        return accountNumber;
    }


}

