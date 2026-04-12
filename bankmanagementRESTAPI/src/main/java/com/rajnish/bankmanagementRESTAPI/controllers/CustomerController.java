package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.dtos.CustomerDto;
import com.rajnish.bankmanagementRESTAPI.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Customer Handling API")
@RestController
@RequestMapping(path = "/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            description = "Add Customer Details API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @PostMapping("/{id}/add_customer_details")
    public ResponseEntity<CustomerDto> addCustomerDetails(@PathVariable long id, @RequestBody CustomerDto customerDto){
        return ResponseEntity.ok(customerService.addCustomerDetails(id,customerDto));
    }

}
