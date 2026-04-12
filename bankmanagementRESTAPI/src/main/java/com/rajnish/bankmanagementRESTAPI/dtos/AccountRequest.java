package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String bankName;
    private String branch;
    private String branchCode;
    private Boolean verified = false;
    private IdentityType identityType;
    private String accountType;
    private String identityNumber;
}
