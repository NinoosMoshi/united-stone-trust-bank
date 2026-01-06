package com.ninos.account.controller;

import com.ninos.account.service.AccountService;
import com.ninos.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/me")
    public ResponseEntity<Response<?>> getMyAccounts(){
        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    @DeleteMapping("/close/{accountNumber}")
    public ResponseEntity<Response<?>> deleteAccount(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }


}

