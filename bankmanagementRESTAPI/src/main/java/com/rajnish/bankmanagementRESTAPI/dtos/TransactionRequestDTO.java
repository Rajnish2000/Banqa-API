package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionMode;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    private Double transaction_amount;
    private TransactionType transactionType;
    private String accountNum;
    private TransactionMode transactionMode;
    private String toAccountNum;
    private String transaction_remark;
}
