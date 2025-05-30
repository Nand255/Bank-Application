package com.ldrp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    name = "Bank Response",
    description = "Standard response format for all banking operations"
)
public class BankResponse {
    @Schema(
        description = "Response code indicating the operation result",
        example = "002"
    )
    private String responseCode;

    @Schema(
        description = "Detailed message about the operation result",
        example = "Account has been successfully created!"
    )
    private String responseMessage;

    @Schema(
        description = "Account information if applicable to the operation"
    )
    private AccountInfo accountInfo;
}