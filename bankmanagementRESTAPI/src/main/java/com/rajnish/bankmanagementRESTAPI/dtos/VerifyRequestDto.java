package com.rajnish.bankmanagementRESTAPI.dtos;

import com.rajnish.bankmanagementRESTAPI.entities.type.IdentityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRequestDto {
    private String identityNumber;
    private IdentityType identityType;
}
