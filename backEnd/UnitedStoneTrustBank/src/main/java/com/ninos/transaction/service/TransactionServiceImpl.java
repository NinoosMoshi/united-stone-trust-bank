package com.ninos.transaction.service;

import com.ninos.account.entity.Account;
import com.ninos.account.repository.AccountRepository;
import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.service.UserService;
import com.ninos.enums.TransactionStatus;
import com.ninos.enums.TransactionType;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.InsufficientBalanceException;
import com.ninos.exceptions.InvalidTransactionException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import com.ninos.response.Response;
import com.ninos.transaction.dto.TransactionDTO;
import com.ninos.transaction.dto.TransactionRequest;
import com.ninos.transaction.entity.Transaction;
import com.ninos.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Transactional
    @Override
    public Response<?> createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());

        switch (transactionRequest.getTransactionType()){
            case DEPOSIT -> handleDeposit(transactionRequest, transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
            case TRANSFER -> handleTransfer(transactionRequest, transaction);
            default -> throw new InvalidTransactionException("Invalid transaction type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // send notification
        sendTransactionNotifications(savedTransaction);

        return Response.builder()
                .statusCode(200)
                .message("Transaction successful")
                .build();
    }



    @Transactional
    @Override
    public Response<List<TransactionDTO>> getTransactionsForMyAccount(String accountNumber, int page, int size) {

        // get current login user
        User user = userService.getCurrentLoggedInUser();

        // Find the account by its number
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account Not Found"));

        // make sure account belong to the user
        if(!account.getUser().getId().equals(user.getId())){
            throw new BadRequestException("Account does not belong to the authenticated user");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactionPage = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);

        List<TransactionDTO> transactionDTOS = transactionPage.getContent().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();

        return Response.<List<TransactionDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transactions retrieved")
                .data(transactionDTOS)
                .meta(Map.of(
                        "currentPage",transactionPage.getNumber(),
                        "totalItems",transactionPage.getTotalElements(),
                        "totalPages",transactionPage.getTotalPages(),
                        "pageSize",transactionPage.getSize()
                ))
                .build();

    }





    private void handleDeposit(TransactionRequest request, Transaction transaction){
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account number not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        transaction.setAccount(account);
        accountRepository.save(account);
    }


    private void handleWithdrawal(TransactionRequest request, Transaction transaction){
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account number not found"));

        if(account.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance in your account");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        transaction.setAccount(account);
        accountRepository.save(account);
    }



    private void handleTransfer(TransactionRequest request, Transaction transaction){

        Account sourceAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("source Account number not found"));

        Account destenationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new NotFoundException("Destination Account number not found"));

        if(sourceAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance in your account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        destenationAccount.setBalance(destenationAccount.getBalance().add(request.getAmount()));
        accountRepository.save(destenationAccount);

        transaction.setAccount(sourceAccount);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setDestinationAccount(destenationAccount.getAccountNumber());
    }


    public void sendTransactionNotifications(Transaction savedTransaction){
        User user = savedTransaction.getAccount().getUser();
        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("amount", savedTransaction.getAmount());
        templateVariables.put("accountNumber", savedTransaction.getAccount().getAccountNumber());
        templateVariables.put("date", savedTransaction.getTransactionDate());
        templateVariables.put("balance", savedTransaction.getAccount().getBalance());

        if(savedTransaction.getTransactionType() == TransactionType.DEPOSIT){
            subject = "Credit Alert";
            template = "credit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationEmailToSendOut,user);
        }
        else if(savedTransaction.getTransactionType() == TransactionType.WITHDRAWAL){
            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationEmailToSendOut,user);
        }
        else if (savedTransaction.getTransactionType() == TransactionType.TRANSFER) {
            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationEmailToSendOut,user);

            // receiver credit alert
            Account destination = accountRepository.findByAccountNumber(savedTransaction.getDestinationAccount())
                    .orElseThrow(() -> new NotFoundException("Destination account not found"));

            User receiver = destination.getUser();

            Map<String, Object> receiverVars = new HashMap<>();
            receiverVars.put("name", receiver.getFirstName());
            receiverVars.put("amount", savedTransaction.getAmount());
            receiverVars.put("accountNumber", destination.getAccountNumber());
            receiverVars.put("date", savedTransaction.getTransactionDate());
            receiverVars.put("balance", destination.getBalance());

            NotificationDTO notificationSendEmailToReceiver = NotificationDTO.builder()
                    .recipient(receiver.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(receiverVars)
                    .build();

            notificationService.sendEmail(notificationSendEmailToReceiver,user);

        }

    }


}
