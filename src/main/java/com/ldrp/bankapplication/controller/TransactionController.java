package com.ldrp.bankapplication.controller;

import com.ldrp.bankapplication.entity.Transaction;
import com.ldrp.bankapplication.service.impl.BankStatement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
@Tag(name = "Bank Statement APIs")
public class TransactionController {

    private BankStatement bankStatement;

    @Operation(
            summary = "Generate Bank Statement",
            description = "Retrieve transactions for an account between specified dates. " +
                    "Supported date formats: yyyy-MM-dd"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved transactions"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid date format or date range"
    )
    @GetMapping
    public List<Transaction> generateBankStatement(
            @Parameter(description = "Account number", example = "1234567890")
            @RequestParam String accountNumber,
            @Parameter(description = "Start date (e.g., 2024-03-25)")
            @RequestParam String startDate,
            @Parameter(description = "End date (e.g., 2024-03-28)")
            @RequestParam String endDate) {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}