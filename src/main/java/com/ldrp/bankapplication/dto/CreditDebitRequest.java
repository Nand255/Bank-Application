package com.ldrp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    name = "Credit/Debit Request",
    description = "Request format for credit and debit operations"
)
public class CreditDebitRequest {
    @Schema(
        description = "Account number for the transaction",
        example = "2025123456"
    )
    private String accountNumber;

    @Schema(
        description = "Amount to be credited or debited",
        example = "1000.00"
    )
    private BigDecimal amount;
}