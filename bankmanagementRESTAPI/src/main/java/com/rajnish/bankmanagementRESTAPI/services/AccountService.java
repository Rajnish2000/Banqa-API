package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.dtos.VerifyRequestDto;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.security.SecureRandom;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAccountNumber() {
        return System.currentTimeMillis() + String.format("%03d", secureRandom.nextInt(1000));
    }

    public AccountResponse createAccount(Long userId, AccountRequest request) {
        User currentUser = currentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new SecurityException("You can only create an account for yourself.");
        }

        Customer customer = customerRepository.findCustomerByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Complete your customer profile before creating an account."));

        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        Account account = Account.builder()
                .bankName(request.getBankName())
                .branch(request.getBranch())
                .identityType(request.getIdentityType())
                .identityNumber(request.getIdentityNumber())
                .branchCode(request.getBranchCode())
                .accountNumber(accountNumber)
                .customer(customer)
                .build();

        return modelMapper.map(accountRepository.save(account), AccountResponse.class);
    }

    public String verifyAccount(Long userId, Long accountNumber, VerifyRequestDto request) throws InvalidAttributeValueException {
        User currentUser = currentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new SecurityException("You can only verify your own account.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber.toString())
                .orElseThrow(() -> new NoSuchElementException("Account not found."));

        if (account.getCustomer() == null || !account.getCustomer().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You can only verify your own account.");
        }
        if (!account.getIdentityType().equals(request.getIdentityType())
                || !account.getIdentityNumber().equals(request.getIdentityNumber())) {
            throw new InvalidAttributeValueException("Verification failed. The identity details do not match.");
        }

        account.setVerified(true);
        accountRepository.save(account);
        return "Account verified successfully.";
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DeleteAccountDto deleteAccount(Long accountNum) {
        String accountNumber = accountNum.toString();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found."));
        if (!Boolean.TRUE.equals(account.getVerified())) {
            throw new IllegalArgumentException("An unverified account cannot be deleted.");
        }

        Customer customer = account.getCustomer();
        DeleteAccountDto response = new DeleteAccountDto(
                accountNumber,
                customer.getUser().getName(),
                account.getBankName(),
                account.getBranch(),
                account.getBranchCode()
        );
        customer.getAccountId().remove(account);
        accountRepository.delete(account);
        return response;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User user)) {
            throw new SecurityException("Authentication is required.");
        }
        return user;
    }
}
