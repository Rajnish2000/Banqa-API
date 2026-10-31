package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private String transactionId;
    private BigDecimal transaction_amount;
    private TransactionType transactionType;
    private String accountNum;
    private String toAccountNum;
    private String transaction_remark;
    private TransactionNature crdr;
    private LocalDateTime transactionTime;

}
