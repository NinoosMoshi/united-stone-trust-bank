package com.ninos.audit_dashboard.controller;

import com.ninos.account.dto.AccountDTO;
import com.ninos.audit_dashboard.service.AuditorService;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.transaction.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
public class AuditorController {

    private final AuditorService auditorService;

    @GetMapping("/totals")
    public ResponseEntity<Map<String, Long>> getSystemTotals(){
        return ResponseEntity.ok(auditorService.getSystemTotal());
    }


    @GetMapping("/users")
    public ResponseEntity<UserDTO> findUserByEmail(@RequestParam String email){
        Optional<UserDTO> userDTO = auditorService.findUserByEmail(email);

//        return userDTO.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/accounts")
    public ResponseEntity<AccountDTO> findAccountDetailsByAccountNumber(@RequestParam String accountNumber){
        Optional<AccountDTO> accountDTO = auditorService.findAccountDetailsByAccountNumber(accountNumber);

        return accountDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/transactions/by-account")
    public ResponseEntity<List<TransactionDTO>> findTransactionsByAccountNumber(@RequestParam String accountNumber){
        List<TransactionDTO> transactionDTOList = auditorService.findTransactionsByAccountNumber(accountNumber);

        if(transactionDTOList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactionDTOList);
    }


    @GetMapping("/transactions/by-id")
    public ResponseEntity<TransactionDTO> findTransactionById(@RequestParam Long transactionId){
        Optional<TransactionDTO> transactionDTO = auditorService.findTransactionById(transactionId);

        return transactionDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}

