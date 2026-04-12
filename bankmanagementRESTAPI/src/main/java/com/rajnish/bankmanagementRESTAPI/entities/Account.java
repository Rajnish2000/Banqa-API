package com.rajnish.bankmanagementRESTAPI.entities;

import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue
    @Column(length = 36, updatable = false, nullable = false)
    private String aid;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String branchCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountType accountType = AccountType.SAVING;

    @Builder.Default
    private Double balance = 0.0;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IdentityType identityType = IdentityType.AADHAR;

    @Column(nullable = false)
    private String identityNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

}
