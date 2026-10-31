package com.rajnish.bankmanagementRESTAPI.entities;

import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionMode;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36, updatable = false, nullable = false)
    private String tid;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transaction_amount;

    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

//    @ManyToOne
//    @JoinColumn(name = "main_account_id",nullable = false)
    @Column(nullable = false, length = 24)
    private String accountNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMode transactionMode;

//    @ManyToOne
//    @JoinColumn(name = "receiving_account_id")
    @Column(length = 24)
    private String toAccountNum;

    @Column(length = 36)
    private String transferReference;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;

    private String transaction_remark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionNature crdr;

}
