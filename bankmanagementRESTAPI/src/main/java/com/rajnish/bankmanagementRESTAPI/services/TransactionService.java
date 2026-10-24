package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.TransactionRequestDTO;
import com.rajnish.bankmanagementRESTAPI.dtos.TransactionResponseDTO;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.Transaction;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionNature;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionStatus;
import com.rajnish.bankmanagementRESTAPI.entities.type.TransactionType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public TransactionResponseDTO withdrawOrDepositMoney(TransactionRequestDTO request) throws InvalidAttributeValueException {
        validateAmount(request.getTransaction_amount());

        User user = currentUser();
        Customer customer = customerRepository.findCustomerByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No customer profile exists for this user."));

        Account account = accountRepository.findByAccountNumber(request.getAccountNum())
                .orElseThrow(() -> new NoSuchElementException("Account not found."));
        if (account.getCustomer() == null || !account.getCustomer().getCid().equals(customer.getCid())) {
            throw new SecurityException("You can only transact from your own account.");
        }
        ensureAccountCanTransact(account);

        Transaction transaction;
        double amount = request.getTransaction_amount();

        if (request.getTransactionType() == TransactionType.DEPOSIT) {
            if (amount >= 1_000_000) {
                throw new IllegalArgumentException("A single deposit must be less than 1,000,000.");
            }
            account.setBalance(safeBalance(account) + amount);
            transaction = buildTransaction(request, account.getAccountNumber(), null, TransactionNature.CR);
        } else if (request.getTransactionType() == TransactionType.WITHDRAWAL) {
            if (safeBalance(account) < amount) {
                throw new IllegalArgumentException("Insufficient balance in account.");
            }
            account.setBalance(safeBalance(account) - amount);
            transaction = buildTransaction(request, account.getAccountNumber(), null, TransactionNature.DR);
        } else {
            throw new InvalidAttributeValueException("Transaction type must be DEPOSIT or WITHDRAWAL.");
        }

        accountRepository.save(account);
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        return modelMapper.map(transactionRepository.save(transaction), TransactionResponseDTO.class);
    }

    @Transactional
    public TransactionResponseDTO transferMoney(TransactionRequestDTO request) throws InvalidAttributeValueException {
        validateAmount(request.getTransaction_amount());
        if (request.getTransactionType() != TransactionType.TRANSFER) {
            throw new InvalidAttributeValueException("Transaction type must be TRANSFER.");
        }
        if (request.getAccountNum() == null || request.getToAccountNum() == null
                || request.getAccountNum().equals(request.getToAccountNum())) {
            throw new IllegalArgumentException("A valid, different sender and receiver account are required.");
        }

        User user = currentUser();
        Customer customer = customerRepository.findCustomerByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No customer profile exists for this user."));

        Account from = accountRepository.findByAccountNumber(request.getAccountNum())
                .orElseThrow(() -> new NoSuchElementException("Sender account not found."));
        Account to = accountRepository.findByAccountNumber(request.getToAccountNum())
                .orElseThrow(() -> new NoSuchElementException("Receiver account not found."));

        if (from.getCustomer() == null || !from.getCustomer().getCid().equals(customer.getCid())) {
            throw new SecurityException("You can only transfer money from your own account.");
        }
        ensureAccountCanTransact(from);
        ensureAccountCanTransact(to);

        double amount = request.getTransaction_amount();
        if (safeBalance(from) < amount) {
            throw new IllegalArgumentException("Insufficient balance in sender account.");
        }

        from.setBalance(safeBalance(from) - amount);
        to.setBalance(safeBalance(to) + amount);
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction debit = buildTransaction(request, from.getAccountNumber(), to.getAccountNumber(), TransactionNature.DR);
        debit.setTransactionStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(debit);

        Transaction credit = buildTransaction(request, to.getAccountNumber(), from.getAccountNumber(), TransactionNature.CR);
        credit.setTransactionStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(credit);

        return modelMapper.map(debit, TransactionResponseDTO.class);
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User user)) {
            throw new SecurityException("Authentication is required.");
        }
        return user;
    }

    private void validateAmount(Double amount) {
        if (amount == null || !Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be a positive, finite number.");
        }
    }

    private double safeBalance(Account account) {
        return account.getBalance() == null ? 0.0 : account.getBalance();
    }

    private void ensureAccountCanTransact(Account account) {
        if (!Boolean.TRUE.equals(account.getVerified())) {
            throw new IllegalArgumentException("Account must be verified before transacting.");
        }
    }

    private Transaction buildTransaction(TransactionRequestDTO request, String accountNumber,
                                         String otherAccountNumber, TransactionNature nature) {
        return Transaction.builder()
                .accountNum(accountNumber)
                .toAccountNum(otherAccountNumber)
                .transaction_amount(request.getTransaction_amount())
                .transactionType(request.getTransactionType())
                .transactionMode(request.getTransactionMode())
                .transaction_remark(request.getTransaction_remark())
                .transactionStatus(TransactionStatus.PENDING)
                .crdr(nature)
                .build();
    }
}
