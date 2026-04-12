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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<AccountResponse> createAccount(@PathVariable Integer id, @RequestBody AccountRequest accountRequest){
        Long uid = (long) id;
        return ResponseEntity.ok(accountService.createAccount(uid,accountRequest));
    }

    @Operation(
            description = "Verify Account API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @PostMapping("/{id}/{accountNumber}/verify_account")
    public ResponseEntity<String> verifyAccount(
            @PathVariable Long id,
            @PathVariable Long accountNumber,
            @RequestBody VerifyRequestDto verifyRequestDto
            ) throws InvalidAttributeValueException {
        return ResponseEntity.ok(accountService.verifyAccount(id, accountNumber, verifyRequestDto));
    }

    @Operation(
            description = "Delete Account API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @DeleteMapping("/{accountNum}/delete_account")
    public ResponseEntity<DeleteAccountDto> deleteAccount(@PathVariable Long accountNum){
        return ResponseEntity.ok(accountService.deleteAccount(accountNum));
    }

}
