package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionMode;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal transaction_amount;
    @NotNull
    private TransactionType transactionType;
    @NotBlank
    @Size(max = 24)
    private String accountNum;
    @NotNull
    private TransactionMode transactionMode;
    @Size(max = 24)
    private String toAccountNum;
    @Size(max = 255)
    private String transaction_remark;
}
