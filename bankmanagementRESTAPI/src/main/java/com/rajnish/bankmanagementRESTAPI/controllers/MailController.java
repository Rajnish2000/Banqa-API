package com.rajnish.bankmanagementRESTAPI.controllers;

import com.rajnish.bankmanagementRESTAPI.dtos.SendMailDTO;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.services.SendMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public String sendOtpToMail(@RequestBody SendMailDTO sendMailDTO) throws Exception {
        String otp = sendMailService.sendOtpEmail(sendMailDTO.getEmail());
        return "OTP sent to " + sendMailDTO.getEmail() + " (for demo, OTP: " + otp + ").Please check your mail.";
    }

    @GetMapping("/verify-otp")
    @Operation(
            description = "Verify OTP API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public String verifyOTP(@RequestParam String inOtp){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getEmail();
        boolean result = sendMailService.verifyOYP(email,inOtp);
        return result ? "Verified Successfully" : "Verification failed";
    }
}
