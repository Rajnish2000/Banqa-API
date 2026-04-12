package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.entities.Account;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.repositories.AccountRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

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
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountNumberSuccessfully(){
        Long userId = 1L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType("SAVING");
        accountRequest.setVerified(false);
        accountRequest.setBranch("SBI2333");
        accountRequest.setBankName("State Bank of India");
        accountRequest.setBranchCode("SBI232");
        accountRequest.setIdentityType(IdentityType.AADHAR);
        accountRequest.setIdentityNumber("123456789");

        String accountNum = "2342352234234";
        Customer customer = new Customer();
        customer.setCid("23fsd-2323423-234");

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
        accountResponse.setAccountType(accountRequest.getAccountType());
        accountResponse.setAccountStatus("ACTIVE");
        accountResponse.setBankName(accountRequest.getBankName());

        // this part called Mock behavior
        when(customerRepository.findCustomerByUserId(userId)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(modelMapper.map(savedAccount,AccountResponse.class)).thenReturn(accountResponse);

        // this part is Act
        AccountResponse result = accountService.createAccount(userId, accountRequest);

        // this part is Assert
        assertNotNull(result);
        assertEquals(accountResponse.getBankName(),result.getBankName());
        assertEquals(accountResponse.getAccountNumber(),result.getAccountNumber());
        assertEquals(accountResponse.getAccountType(),result.getAccountType());

        // this part is Verify
        verify(customerRepository).findCustomerByUserId(userId);
        verify(accountRepository).findByAccountNumber(anyString());
        verify(accountRepository).save(any(Account.class));
        verify(modelMapper).map(savedAccount,AccountResponse.class);
    }

    @Test
    void shouldAccountDeletedSuccessfully(){

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
        account.setCustomer(customer);
        account.setBankName("HDFC");
        account.setBranch("Mumbai");
        account.setBranchCode("HDFC001");

        customer.getAccountId().add(account);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        DeleteAccountDto result = accountService.deleteAccount(accountNum);

        assertNotNull(result);
        assertEquals(accountNumber,result.getAccountNumber());
        assertEquals(account.getCustomer().getUser().getName(),result.getCustomerName());

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository).flush();

    }

}