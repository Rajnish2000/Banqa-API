package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.dtos.TransactionRequestDTO;
import com.rajnish.bankmanagementRESTAPI.dtos.TransactionResponseDTO;
import com.rajnish.bankmanagementRESTAPI.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidAttributeValueException;

@Tag(name="Transaction API")
@RestController
@RequestMapping(path = "/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            description = "Deposit and WithDraw API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @PostMapping("/withdraw_or_deposit_money")
    public TransactionResponseDTO withdrawOrDepositMoney(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) throws InvalidAttributeValueException {
        return transactionService.withdrawOrDepositMoney(transactionRequestDTO);
    }

    @PostMapping("/transfer_money")
    @Operation(
            description = "Transfer Money API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public TransactionResponseDTO transferMoney(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) throws InvalidAttributeValueException {
        return transactionService.transferMoney(transactionRequestDTO);
    }
}
