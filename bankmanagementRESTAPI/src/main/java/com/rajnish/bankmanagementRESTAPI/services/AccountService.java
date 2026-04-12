package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.dtos.VerifyRequestDto;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;

import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.InputMismatchException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.nio.InvalidMarkException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public String generateAccountNumber() {
        long timestamp = System.currentTimeMillis();
        int randomNum = new SecureRandom().nextInt(999);
        return timestamp + String.format("%03d", randomNum);
    }

    public AccountResponse createAccount(Long id, AccountRequest accountRequest) {
        Customer customer = customerRepository.findCustomerByUserId(id).orElse(null);
        System.out.println("customer : "+customer);
        String accountNum = generateAccountNumber();
        Account account = accountRepository.findByAccountNumber(accountNum).orElse(null);
        while(account != null){
            accountNum = generateAccountNumber();
            account = accountRepository.findByAccountNumber(accountNum).orElse(null);
        }
        account = accountRepository.save(Account.builder()
                        .bankName(accountRequest.getBankName())
                        .branch(accountRequest.getBranch())
                        .identityType(accountRequest.getIdentityType())
                        .identityNumber(accountRequest.getIdentityNumber())
                        .branchCode(accountRequest.getBranchCode())
                        .accountNumber(accountNum)
                        .customer(customer)
                        .build()
                );
        return modelMapper.map(account, AccountResponse.class);
    }

    public String verifyAccount(Long id, Long accountNumber, VerifyRequestDto verifyRequestDto) throws InvalidAttributeValueException {
        String accNum = accountNumber.toString();
        Account account = accountRepository.findByAccountNumber(accNum).orElseThrow(InvalidMarkException::new);
        if(!account.getIdentityType().equals(verifyRequestDto.getIdentityType()) || !account.getIdentityNumber().equals(verifyRequestDto.getIdentityNumber())){
            throw new InvalidAttributeValueException("Verification Failed. Invalid details passed.");
        }
        account.setVerified(true);
        accountRepository.save(account);
        return "Account verified successfully.";
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DeleteAccountDto deleteAccount(Long accountNum) {
        String accountNumber = accountNum.toString();
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        if(!account.getVerified()){
            throw new IllegalArgumentException("Unverified account can not be deleted.");
        }
        Customer customer = account.getCustomer();
        customer.getAccountId().remove(account);
        accountRepository.delete(account);
        accountRepository.flush();
        System.out.println("account: "+account.getAccountNumber());
        return new DeleteAccountDto(accountNumber,account.getCustomer().getUser().getName(),account.getBankName(),account.getBranch(),account.getBranchCode());
    }
}
