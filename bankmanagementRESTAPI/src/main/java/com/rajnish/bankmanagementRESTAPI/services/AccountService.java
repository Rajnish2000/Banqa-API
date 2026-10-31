package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.dtos.VerifyRequestDto;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public String generateAccountNumber() {
        return Long.toString(100_000_000_000L + SECURE_RANDOM.nextLong(900_000_000_000L));
    }

    @Transactional
    public AccountResponse createAccount(Long userId, AccountRequest accountRequest) {
        User authenticatedUser = currentUser();
        if (!authenticatedUser.getId().equals(userId)
                && authenticatedUser.getRole() != User.UserRole.ADMIN) {
            throw new AccessDeniedException("You cannot create an account for another user.");
        }

        Customer customer = customerRepository.findCustomerByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Customer profile not found."));
        if (!Boolean.TRUE.equals(customer.getKyc_status())) {
            throw new IllegalArgumentException("Customer KYC must be approved before opening an account.");
        }
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder()
                .bankName(accountRequest.getBankName())
                .branch(accountRequest.getBranch())
                .identityType(accountRequest.getIdentityType())
                .identityNumber(accountRequest.getIdentityNumber())
                .branchCode(accountRequest.getBranchCode())
                .accountType(accountRequest.getAccountType())
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .customer(customer)
                .build();
        Account savedAccount = accountRepository.save(account);
        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getAccountType().name(),
                savedAccount.getBranchCode(),
                savedAccount.getAccountStatus().name(),
                savedAccount.getBalance(),
                savedAccount.getBankName(),
                savedAccount.getBranch(),
                savedAccount.getVerified(),
                savedAccount.getIdentityType().name());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String verifyAccount(Long accountNumber, VerifyRequestDto request)
            throws InvalidAttributeValueException {
        Account account = accountRepository.findByAccountNumber(accountNumber.toString())
                .orElseThrow(() -> new NoSuchElementException("Account not found."));
        if (account.getIdentityType() != request.getIdentityType()
                || !account.getIdentityNumber().equals(request.getIdentityNumber())) {
            throw new InvalidAttributeValueException("Verification failed. Identity details do not match.");
        }
        if (!Boolean.TRUE.equals(account.getCustomer().getKyc_status())) {
            throw new IllegalArgumentException("Customer KYC must be approved before account verification.");
        }
        account.setVerified(true);
        accountRepository.save(account);
        return "Account verified successfully.";
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DeleteAccountDto closeAccount(Long accountNum) {
        String accountNumber = accountNum.toString();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found."));
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("An account must have a zero balance before it can be closed.");
        }
        account.setAccountStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
        return new DeleteAccountDto(
                accountNumber,
                account.getCustomer().getUser().getName(),
                account.getBankName(),
                account.getBranch(),
                account.getBranchCode());
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Authentication is required.");
        }
        return user;
    }
}
