package com.ldrp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    name = "User Request",
    description = "Request format for creating a new bank account"
)
public class UserRequest {
    @Schema(
        description = "User's first name",
        example = "John"
    )
    private String firstName;

    @Schema(
        description = "User's last name",
        example = "Doe"
    )
    private String lastName;

    @Schema(
        description = "User's middle name or other names",
        example = "Michael"
    )
    private String otherName;

    @Schema(
        description = "User's gender",
        example = "Male"
    )
    private String gender;

    @Schema(
        description = "User's residential address",
        example = "123 Main St, City, Country"
    )
    private String address;

    @Schema(
        description = "User's state of origin",
        example = "California"
    )
    private String stateOfOrigin;

    @Schema(
        description = "User's email address",
        example = "john.doe@example.com"
    )
    private String email;

    private String password;

    @Schema(
        description = "User's primary phone number",
        example = "+1234567890"
    )
    private String phoneNumber;

    @Schema(
        description = "User's alternative phone number",
        example = "+1987654321"
    )
    private String alternativePhoneNumber;

    @Schema(
        description = "User's Aadhar card number",
        example = "1234 5678 9012"
    )
    private String aadharNumber;
}