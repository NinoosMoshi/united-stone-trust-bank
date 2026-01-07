package com.ninos.transaction.service;

import com.ninos.response.Response;
import com.ninos.transaction.dto.TransactionDTO;
import com.ninos.transaction.dto.TransactionRequest;
import java.util.List;


public interface TransactionService {

    Response<?> createTransaction(TransactionRequest transactionRequest);

    Response<List<TransactionDTO>> getTransactionsForMyAccount(String accountNumber, int page, int size);

}

