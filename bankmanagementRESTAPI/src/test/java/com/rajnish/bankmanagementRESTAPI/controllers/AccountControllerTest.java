package com.rajnish.bankmanagementRESTAPI.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.dtos.VerifyRequestDto;
import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import com.rajnish.bankmanagementRESTAPI.entities.type.AccountType;
import com.rajnish.bankmanagementRESTAPI.services.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.rajnish.bankmanagementRESTAPI.jwt.JwtAuthFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAccount() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setBankName("HDFC");
        request.setBranch("Mumbai");
        request.setIdentityType(IdentityType.AADHAR);
        request.setIdentityNumber("1234567890");
        request.setBranchCode("HDFC001");
        request.setAccountType(AccountType.SAVING);

        AccountResponse response = new AccountResponse();
        response.setAccountNumber("12345678901");

        when(accountService.createAccount(eq(1L), any(AccountRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/account/1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("12345678901"));

    }

    @Test
    void shouldVerifyAccountSuccessfully() throws Exception {
        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setIdentityType(IdentityType.AADHAR);
        verifyRequestDto.setIdentityNumber("1234567890");

        when(accountService.verifyAccount(eq(12345432112345L), any(VerifyRequestDto.class))).thenReturn("Account verified successfully.");
        mockMvc.perform(post("/account/12345432112345/verify_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Account verified successfully."));
    }

    @Test
    void shouldCloseZeroBalanceAccountSuccessfully() throws Exception {

        DeleteAccountDto response = new DeleteAccountDto();
        response.setAccountNumber("12345");
        response.setCustomerName("John Doe");
        response.setBankName("HDFC");
        response.setBranch("Mumbai");
        response.setBranchCode("HDFC001");

        when(accountService.closeAccount(eq(1L)))
                .thenReturn(response);

        mockMvc.perform(delete("/account/1/delete_account")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("12345"))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.bankName").value("HDFC"))
                .andExpect(jsonPath("$.branch").value("Mumbai"))
                .andExpect(jsonPath("$.branchCode").value("HDFC001"));
    }

}