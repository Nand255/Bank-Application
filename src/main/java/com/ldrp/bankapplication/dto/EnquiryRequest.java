package com.ldrp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    name = "Enquiry Request",
    description = "Request format for account enquiry operations"
)
public class EnquiryRequest {
    @Schema(
        description = "Account number to query",
        example = "2025123456"
    )
    private String accountNumber;
}