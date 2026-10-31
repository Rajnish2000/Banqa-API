package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.TransactionRequestDTO;
import com.rajnish.bankmanagementRESTAPI.dtos.TransactionResponseDTO;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.Transaction;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionMode;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private TransactionService transactionService;

    private User owner;
    private Account sender;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(11L)
                .email("owner@example.com")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(owner, null, owner.getAuthorities()));
        sender = account("1001", owner, "100.00");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void depositAddsAmountToExistingBalance() {
        stubTransactionSave();
        when(accountRepository.lockByAccountNumbers(anyCollection())).thenReturn(List.of(sender));

        TransactionResponseDTO result = transactionService.withdrawOrDepositMoney(
                request("25.50", TransactionType.DEPOSIT, "1001", null));

        assertEquals(new BigDecimal("125.50"), sender.getBalance());
        assertEquals(TransactionNature.CR, result.getCrdr());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawalAllowsExactBalanceAndCannotOverdraw() {
        stubTransactionSave();
        sender.setBalance(new BigDecimal("100.00"));
        when(accountRepository.lockByAccountNumbers(anyCollection())).thenReturn(List.of(sender));

        transactionService.withdrawOrDepositMoney(
                request("100.00", TransactionType.WITHDRAWAL, "1001", null));

        assertEquals(BigDecimal.ZERO.setScale(2), sender.getBalance());
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawOrDepositMoney(
                        request("0.01", TransactionType.WITHDRAWAL, "1001", null)));
    }

    @Test
    void transferDebitsSourceAndCreditsDestination() {
        stubTransactionSave();
        Account receiver = account("2002", owner, "10.00");
        when(accountRepository.lockByAccountNumbers(anyCollection()))
                .thenReturn(List.of(receiver, sender));

        TransactionResponseDTO result = transactionService.transferMoney(
                request("30.00", TransactionType.TRANSFER, "1001", "2002"));

        assertEquals(new BigDecimal("70.00"), sender.getBalance());
        assertEquals(new BigDecimal("40.00"), receiver.getBalance());
        assertEquals("1001", result.getAccountNum());
        assertEquals("2002", result.getToAccountNum());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void userCannotWithdrawFromAnotherUsersAccount() {
        User otherUser = User.builder().id(22L).role(User.UserRole.USER).build();
        Account otherAccount = account("9009", otherUser, "100.00");
        when(accountRepository.lockByAccountNumbers(anyCollection())).thenReturn(List.of(otherAccount));

        assertThrows(SecurityException.class, () ->
                transactionService.withdrawOrDepositMoney(
                        request("1.00", TransactionType.WITHDRAWAL, "9009", null)));
        assertEquals(new BigDecimal("100.00"), otherAccount.getBalance());
    }

    private Account account(String number, User user, String balance) {
        Customer customer = Customer.builder().user(user).kyc_status(true).build();
        return Account.builder()
                .accountNumber(number)
                .customer(customer)
                .balance(new BigDecimal(balance))
                .verified(true)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    private TransactionRequestDTO request(
            String amount, TransactionType type, String from, String to) {
        return new TransactionRequestDTO(
                new BigDecimal(amount), type, from, TransactionMode.NET_BANKING, to, "test");
    }

    private void stubTransactionSave() {
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
