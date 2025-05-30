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
    name = "Account Information",
    description = "Data transfer object containing essential account information"
)
public class AccountInfo {
    
    @Schema(
        description = "Full name of the account holder",
        example = "John Michael Doe"
    )
    private String accountName;
    
    @Schema(
        description = "Current balance in the account",
        example = "5000.00"
    )
    private BigDecimal accountBalance;
    
    @Schema(
        description = "Unique account number",
        example = "2025123456"
    )
    private String accountNumber;
}