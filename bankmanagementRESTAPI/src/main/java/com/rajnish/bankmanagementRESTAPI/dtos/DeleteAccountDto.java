package com.rajnish.bankmanagementRESTAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountDto {
    private String accountNumber;
    private String customerName;
    private String bankName;
    private String branch;
    private String branchCode;
}
