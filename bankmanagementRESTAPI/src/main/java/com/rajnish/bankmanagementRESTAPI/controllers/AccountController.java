package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.dtos.AccountRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AccountResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.DeleteAccountDto;
import com.rajnish.bankmanagementRESTAPI.dtos.VerifyRequestDto;
import com.rajnish.bankmanagementRESTAPI.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidAttributeValueException;

@Tag(name="Accounting API")
@RestController
@RequestMapping(path = "/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{id}/create")
    @Operation(
            description = "Create Customer Account API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<AccountResponse> createAccount(@PathVariable Long id, @Valid @RequestBody AccountRequest accountRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(id,accountRequest));
    }

    @Operation(
            description = "Verify Account API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @PostMapping("/{accountNumber}/verify_account")
    public ResponseEntity<String> verifyAccount(
            @PathVariable Long accountNumber,
            @Valid @RequestBody VerifyRequestDto verifyRequestDto
            ) throws InvalidAttributeValueException {
        return ResponseEntity.ok(accountService.verifyAccount(accountNumber, verifyRequestDto));
    }

    @PostMapping("/{userId}/{accountNumber}/verify_account")
    public ResponseEntity<String> verifyAccountLegacyRoute(
            @PathVariable Long userId,
            @PathVariable Long accountNumber,
            @Valid @RequestBody VerifyRequestDto verifyRequestDto)
            throws InvalidAttributeValueException {
        return verifyAccount(accountNumber, verifyRequestDto);
    }

    @Operation(
            description = "Close a zero-balance account (the account is retained for audit history).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @DeleteMapping("/{accountNum}/delete_account")
    public ResponseEntity<DeleteAccountDto> closeAccount(@PathVariable Long accountNum){
        return ResponseEntity.ok(accountService.closeAccount(accountNum));
    }

}
