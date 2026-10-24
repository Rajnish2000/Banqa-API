package com.rajnish.bankmanagementRESTAPI.entities;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionMode;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue
    @Column(length = 36, updatable = false, nullable = false)
    private String tid;

    @Column(nullable = false)
    @Positive
    private Double transaction_amount;

    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    private String accountNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMode transactionMode;

    private String toAccountNum;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;

    private String transaction_remark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionNature crdr;

    @AssertTrue(message = "Receiving account must be provided for transfer transactions")
    public boolean isReceivingAccountValid() {
        return !TransactionType.TRANSFER.equals(this.transactionType) || this.toAccountNum != null;
    }

    @AssertTrue(message = "Transfer direction must be set explicitly")
    public boolean isCrDrConsistent() {
        return !TransactionType.TRANSFER.equals(this.transactionType) || this.crdr != null;
    }
}
