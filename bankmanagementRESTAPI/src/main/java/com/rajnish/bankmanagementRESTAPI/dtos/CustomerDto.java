package com.rajnish.bankmanagementRESTAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    @NotNull(message = "mobile number can not be null")
    private String mobile;
    private String address;
    @NotBlank
    @NotNull
    private String gender;
    @NotBlank
    @NotNull
    private LocalDate dob;
    private Boolean kyc_status;
}
