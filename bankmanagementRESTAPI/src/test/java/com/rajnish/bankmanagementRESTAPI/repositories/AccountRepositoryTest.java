package com.rajnish.bankmanagementRESTAPI.repositories;

import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .address("Test Customer")
                .kyc_status(true)
                .build();

        entityManager.persistAndFlush(customer);
    }

    @Test
    void shouldFindAccountByAccountNumber() {

        Account account = Account.builder()
                .accountNumber("123456789")
                .bankName("Test Bank")
                .branch("Test Branch")
                .branchCode("001")
                .accountType(AccountType.SAVING)
                .balance(1000.0)
                .customer(customer)
                .verified(true)
                .identityType(IdentityType.AADHAR)
                .identityNumber("1234-5678-9012")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        entityManager.persistAndFlush(account);

        Account foundAccount = accountRepository.findByAccountNumber("123456789").orElse(null);

        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getAccountNumber()).isEqualTo("123456789");
    }
}