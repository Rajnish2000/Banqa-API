package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.CustomerDto;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CustomerDto addCustomerDetails(Long userId, CustomerDto customerDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User currentUser) || !currentUser.getId().equals(userId)) {
            throw new SecurityException("You can only create your own customer profile.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (customerRepository.findCustomerByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Customer profile already exists for this user.");
        }

        Customer customer = Customer.builder()
                .user(user)
                .mobile(customerDto.getMobile())
                .gender(customerDto.getGender())
                .address(customerDto.getAddress())
                .dob(customerDto.getDob())
                // KYC status must be set by a trusted verification process, not by client input.
                .kyc_status(false)
                .build();

        return modelMapper.map(customerRepository.save(customer), CustomerDto.class);
    }
}
