package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Double transaction_amount;
    private TransactionType transactionType;
    private String accountNum;
    private String transaction_remark;

}

