package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    @NotBlank
    private String bankName;
    @NotBlank
    private String branch;
    @NotBlank
    private String branchCode;
    @NotNull
    private IdentityType identityType;
    @NotNull
    private AccountType accountType;
    @NotBlank
    private String identityNumber;
}
