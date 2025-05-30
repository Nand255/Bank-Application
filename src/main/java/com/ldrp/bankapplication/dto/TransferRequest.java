package com.ldrp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    name = "Transfer Request",
    description = "Request object for transferring money between bank accounts"
)
public class TransferRequest {
    
    @Schema(
        description = "Account number from which the money will be debited",
        example = "2025123456",
        required = true
    )
    private String sourceAccountNumber;
    
    @Schema(
        description = "Account number to which the money will be credited",
        example = "2025789012",
        required = true
    )
    private String destinationAccountNumber;
    
    @Schema(
        description = "Amount to be transferred between accounts",
        example = "1000.00",
        required = true,
        minimum = "0.01"
    )
    private BigDecimal amount;
}