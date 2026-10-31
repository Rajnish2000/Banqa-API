package com.rajnish.bankmanagementRESTAPI.repositories;

import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .password("Test@123")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        entityManager.persistAndFlush(user);

        customer = Customer.builder()
                .user(user)
                .mobile("9876543210")
                .address("Test Customer")
                .kyc_status(true)
                .build();

        entityManager.persistAndFlush(customer);
        entityManager.flush();
    }

    @Test
    void shouldFindAccountByAccountNumber() {

        Account account = Account.builder()
                .accountNumber("123456789")
                .bankName("Test Bank")
                .branch("Test Branch")
                .branchCode("001")
                .accountType(AccountType.SAVING)
                .balance(new BigDecimal("1000.00"))
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