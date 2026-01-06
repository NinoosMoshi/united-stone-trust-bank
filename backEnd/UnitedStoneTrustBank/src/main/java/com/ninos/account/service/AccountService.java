package com.ninos.account.service;

import com.ninos.account.dto.AccountDTO;
import com.ninos.account.entity.Account;
import com.ninos.auth_users.entity.User;
import com.ninos.enums.AccountType;
import com.ninos.response.Response;


import java.util.List;

public interface AccountService {

    Account createAccount(AccountType accountType, User user);

    Response<List<AccountDTO>> getMyAccounts();

    Response<?> closeAccount(String accountNumber);

}
