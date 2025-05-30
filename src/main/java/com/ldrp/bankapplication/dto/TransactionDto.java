package com.ldrp.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String accountNumber;
    private String status;
}
