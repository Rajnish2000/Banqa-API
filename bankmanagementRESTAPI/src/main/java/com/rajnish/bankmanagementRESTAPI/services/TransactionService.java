package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.TransactionRequestDTO;
import com.rajnish.bankmanagementRESTAPI.dtos.TransactionResponseDTO;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Transaction;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final BigDecimal MAX_BALANCE =
            new BigDecimal("99999999999999999.99");

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponseDTO withdrawOrDepositMoney(TransactionRequestDTO request) {
        validateAmount(request.getTransaction_amount());
        if (request.getTransactionType() != TransactionType.DEPOSIT
                && request.getTransactionType() != TransactionType.WITHDRAWAL) {
            throw new IllegalArgumentException("Only deposits and withdrawals are supported by this operation.");
        }

        Account account = lockAccounts(request.getAccountNum()).get(request.getAccountNum());
        requireOwner(account, currentUser());
        requireUsableAccount(account);

        BigDecimal amount = request.getTransaction_amount();
        TransactionNature nature;
        if (request.getTransactionType() == TransactionType.DEPOSIT) {
            BigDecimal newBalance = account.getBalance().add(amount);
            requireBalanceFits(newBalance);
            account.setBalance(newBalance);
            nature = TransactionNature.CR;
        } else {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in account.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            nature = TransactionNature.DR;
        }

        accountRepository.save(account);
        Transaction transaction = saveTransaction(
                request, account.getAccountNumber(), null, nature, null);
        return toResponse(transaction);
    }

    @Transactional
    public TransactionResponseDTO transferMoney(TransactionRequestDTO request) {
        validateAmount(request.getTransaction_amount());
        if (request.getTransactionType() != TransactionType.TRANSFER) {
            throw new IllegalArgumentException("Transaction type must be TRANSFER.");
        }

        String fromNumber = request.getAccountNum();
        String toNumber = request.getToAccountNum();
        if (toNumber == null || toNumber.isBlank()) {
            throw new IllegalArgumentException("A destination account is required.");
        }
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Source and destination accounts must be different.");
        }

        Map<String, Account> accounts = lockAccounts(fromNumber, toNumber);
        Account fromAccount = accounts.get(fromNumber);
        Account toAccount = accounts.get(toNumber);
        requireOwner(fromAccount, currentUser());
        requireUsableAccount(fromAccount);
        requireUsableAccount(toAccount);

        BigDecimal amount = request.getTransaction_amount();
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account.");
        }

        BigDecimal receiverBalance = toAccount.getBalance().add(amount);
        requireBalanceFits(receiverBalance);
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(receiverBalance);
        accountRepository.saveAll(List.of(fromAccount, toAccount));

        String transferReference = UUID.randomUUID().toString();
        Transaction debit = saveTransaction(
                request, fromNumber, toNumber, TransactionNature.DR, transferReference);
        saveTransaction(request, toNumber, fromNumber, TransactionNature.CR, transferReference);
        return toResponse(debit);
    }

    private Map<String, Account> lockAccounts(String... accountNumbers) {
        List<Account> accounts = accountRepository.lockByAccountNumbers(List.of(accountNumbers));
        Map<String, Account> byNumber = accounts.stream()
                .collect(Collectors.toMap(Account::getAccountNumber, Function.identity()));
        for (String accountNumber : accountNumbers) {
            if (!byNumber.containsKey(accountNumber)) {
                throw new NoSuchElementException("Account not found.");
            }
        }
        return byNumber;
    }

    private void requireOwner(Account account, User user) {
        if (!account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new SecurityException("You cannot transact from an account you do not own.");
        }
    }

    private void requireUsableAccount(Account account) {
        if (!Boolean.TRUE.equals(account.getVerified())
                || account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Account must be verified and active.");
        }
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new SecurityException("Authentication is required.");
        }
        return user;
    }

    private Transaction saveTransaction(
            TransactionRequestDTO request,
            String accountNumber,
            String counterpartyAccount,
            TransactionNature nature,
            String transferReference) {
        Transaction transaction = Transaction.builder()
                .accountNum(accountNumber)
                .toAccountNum(counterpartyAccount)
                .transaction_amount(request.getTransaction_amount())
                .transactionType(request.getTransactionType())
                .transactionMode(request.getTransactionMode())
                .transaction_remark(request.getTransaction_remark())
                .transactionStatus(TransactionStatus.COMPLETED)
                .crdr(nature)
                .transferReference(transferReference)
                .build();
        return transactionRepository.save(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2
                || amount.precision() - amount.scale() > 17) {
            throw new IllegalArgumentException("Amount must be positive and have at most two decimal places.");
        }
    }

    private void requireBalanceFits(BigDecimal balance) {
        if (balance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException("The resulting balance exceeds the supported account limit.");
        }
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getTid(),
                transaction.getTransaction_amount(),
                transaction.getTransactionType(),
                transaction.getAccountNum(),
                transaction.getToAccountNum(),
                transaction.getTransaction_remark(),
                transaction.getCrdr(),
                transaction.getTransactionTime());
    }
}
