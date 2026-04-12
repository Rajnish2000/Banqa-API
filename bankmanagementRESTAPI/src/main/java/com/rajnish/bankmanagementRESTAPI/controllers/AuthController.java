package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.dtos.AuthRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AuthResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.RegisterRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.RegisterResponse;
import com.rajnish.bankmanagementRESTAPI.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Auth API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    //Describes an API endpoint (summary + details)
    @Operation(
            description = "Login API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest requestData) {
        return ResponseEntity.ok(authService.login(requestData));
    }
    @Operation(
            description = "Register API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }
}
