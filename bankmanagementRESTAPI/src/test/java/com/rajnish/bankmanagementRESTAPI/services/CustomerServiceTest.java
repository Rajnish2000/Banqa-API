package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.CustomerDto;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldAddCustomerDetailsSuccessfully(){

        // Arrange
        Long userId = 1L;
        CustomerDto inputDto = new CustomerDto();
        inputDto.setMobile("9999999999");
        inputDto.setGender("MALE");
        inputDto.setDob(LocalDate.of(2000,7,21));
        inputDto.setAddress("Mumbai");
        inputDto.setKyc_status(false);

        User user = new User();
        user.setId(userId);
        user.setRole(User.UserRole.USER);
        user.setEmailVerified(true);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        Customer savedCustomer = Customer.builder()
                .user(user)
                .mobile(inputDto.getMobile())
                .gender(inputDto.getGender())
                .address(inputDto.getAddress())
                .dob(inputDto.getDob())
                .kyc_status(inputDto.getKyc_status())
                .build();

        CustomerDto outputDto = new CustomerDto();
        outputDto.setMobile(savedCustomer.getMobile());
        outputDto.setDob(savedCustomer.getDob());
        outputDto.setGender(savedCustomer.getGender());
        outputDto.setKyc_status(savedCustomer.getKyc_status());

        // Mock behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(modelMapper.map(savedCustomer, CustomerDto.class)).thenReturn(outputDto);

        // Act
        CustomerDto result = customerService.addCustomerDetails(userId, inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(outputDto.getMobile(), result.getMobile());
        assertEquals(outputDto.getAddress(),result.getAddress());
        assertEquals(outputDto.getDob(), result.getDob());

        // Verify interactions
        verify(userRepository).findById(userId);
        verify(customerRepository).save(any(Customer.class));
        verify(modelMapper).map(savedCustomer, CustomerDto.class);
        SecurityContextHolder.clearContext();
    }
}