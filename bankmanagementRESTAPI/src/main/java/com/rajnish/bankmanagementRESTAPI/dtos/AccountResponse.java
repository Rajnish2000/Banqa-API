package com.rajnish.bankmanagementRESTAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private String branchCode;
    private String accountStatus;
    private BigDecimal balance;
    private String bankName;
    private String branch;
    private Boolean verified;
    private String identityType;

}
