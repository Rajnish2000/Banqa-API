package com.rajnish.bankmanagementRESTAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private String branchCode;
    private String accountStatus;
    private Double balance;
    private String bankName;
    private String branch;
    private Boolean verified;
    private String identityType;
    private String identityNumber;

}
