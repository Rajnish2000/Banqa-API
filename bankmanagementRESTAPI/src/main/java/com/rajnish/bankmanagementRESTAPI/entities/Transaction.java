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

//    @ManyToOne
//    @JoinColumn(name = "main_account_id",nullable = false)
    private String accountNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMode transactionMode;

//    @ManyToOne
//    @JoinColumn(name = "receiving_account_id")
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
        if (TransactionType.TRANSFER.equals(this.transactionType)) {
            return this.toAccountNum != null;
        }
        return true;
    }

    @AssertTrue(message = "Sender must be DR and receiver must be CR for transfers")
    public boolean isCrDrConsistent() {
        if (TransactionType.TRANSFER.equals(this.transactionType)) {
            // Sender cannot be CR
            return this.accountNum == null || this.crdr != TransactionNature.CR;
        }
        return true;
    }

    @PrePersist
    private void setCrDrAutomatically() {
        if (this.transactionType == TransactionType.DEPOSIT) {
            this.crdr = TransactionNature.CR;
        } else if (this.transactionType == TransactionType.WITHDRAWAL) {
            this.crdr = TransactionNature.DR;
        } else if (this.transactionType == TransactionType.TRANSFER) {
            // If this is the sender, DR; if it's the receiver, CR
            if (this.toAccountNum != null && this.accountNum != null) {
                this.crdr = TransactionNature.DR; // sender
            } else if (this.accountNum != null) {
                this.crdr = TransactionNature.CR; // receiver
            }
        }
    }

}
