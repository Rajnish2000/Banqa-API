package com.rajnish.bankmanagementRESTAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    @NotBlank(message = "mobile number is required")
    @Pattern(regexp = "^[0-9+() -]{7,20}$")
    private String mobile;
    private String address;
    @NotBlank
    private String gender;
    @NotNull
    @Past
    private LocalDate dob;
    private Boolean kyc_status;
}
