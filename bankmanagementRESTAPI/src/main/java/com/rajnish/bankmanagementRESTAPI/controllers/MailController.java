package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.services.SendMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Mailing Service API")
@RestController
@RequestMapping(path = "/mail")
@RequiredArgsConstructor
public class MailController {

    private final SendMailService sendMailService;

    @PostMapping("/send_otp")
    @Operation(
            description = "Send OTP API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "OTP sent"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> sendOtpToMail() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sendMailService.sendOtpEmail(user.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-otp")
    @Operation(
            description = "Verify OTP API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public ResponseEntity<Void> verifyOTP(@Valid @RequestBody VerifyOtpRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!sendMailService.verifyOtp(user.getEmail(), request.otp())) {
            throw new IllegalArgumentException("OTP is invalid or expired.");
        }
        return ResponseEntity.noContent().build();
    }

    public record VerifyOtpRequest(@NotBlank String otp) {
    }
}
