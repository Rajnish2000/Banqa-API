package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountNumberSuccessfully(){
        Long userId = 1L;
        User owner = User.builder().id(userId).role(User.UserRole.USER).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(owner, null, owner.getAuthorities()));
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.SAVING);
        accountRequest.setBranch("SBI2333");
        accountRequest.setBankName("State Bank of India");
        accountRequest.setBranchCode("SBI232");
        accountRequest.setIdentityType(IdentityType.AADHAR);
        accountRequest.setIdentityNumber("123456789");

        String accountNum = "2342352234234";
        Customer customer = new Customer();
        customer.setCid("23fsd-2323423-234");
        customer.setKyc_status(true);

        Account savedAccount = Account.builder()
                .bankName(accountRequest.getBankName())
                .branch(accountRequest.getBranch())
                .identityType(accountRequest.getIdentityType())
                .identityNumber(accountRequest.getIdentityNumber())
                .branchCode(accountRequest.getBranchCode())
                .accountNumber(accountNum)
                .customer(customer)
                .build();

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(accountNum);
        accountResponse.setAccountType(accountRequest.getAccountType().name());
        accountResponse.setAccountStatus("ACTIVE");
        accountResponse.setBankName(accountRequest.getBankName());

        // this part called Mock behavior
        when(customerRepository.findCustomerByUserId(userId)).thenReturn(Optional.of(customer));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        // this part is Act
        AccountResponse result = accountService.createAccount(userId, accountRequest);

        // this part is Assert
        assertNotNull(result);
        assertEquals(accountResponse.getBankName(),result.getBankName());
        assertEquals(accountResponse.getAccountNumber(),result.getAccountNumber());
        assertEquals(accountResponse.getAccountType(),result.getAccountType());

        // this part is Verify
        verify(customerRepository).findCustomerByUserId(userId);
        verify(accountRepository).existsByAccountNumber(anyString());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldCloseZeroBalanceAccountSuccessfully(){

        Long accountNum = 323423423423L;
        String accountNumber = accountNum.toString();

        User user = new User();
        user.setName("Rajnish");

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setAccountId(new ArrayList<>());

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setVerified(true);
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(customer);
        account.setBankName("HDFC");
        account.setBranch("Mumbai");
        account.setBranchCode("HDFC001");

        customer.getAccountId().add(account);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        DeleteAccountDto result = accountService.closeAccount(accountNum);

        assertNotNull(result);
        assertEquals(accountNumber,result.getAccountNumber());
        assertEquals(account.getCustomer().getUser().getName(),result.getCustomerName());
        assertEquals(com.rajnish.bankmanagementRESTAPI.entities.type.AccountStatus.INACTIVE,
                account.getAccountStatus());

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository).save(account);

    }

}