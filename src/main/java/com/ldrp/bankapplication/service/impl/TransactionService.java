package com.ldrp.bankapplication.service.impl;

import com.ldrp.bankapplication.dto.TransactionDto;
import com.ldrp.bankapplication.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
