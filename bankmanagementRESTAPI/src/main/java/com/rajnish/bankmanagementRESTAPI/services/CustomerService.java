package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.CustomerDto;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CustomerDto addCustomerDetails(Long id, CustomerDto customerDto) {
        User currentUser = currentUser();
        if (!currentUser.getId().equals(id) && currentUser.getRole() != User.UserRole.ADMIN) {
            throw new AccessDeniedException("You cannot create a profile for another user.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Verify the account email before creating a customer profile.");
        }
        if (customerRepository.findCustomerByUserId(id).isPresent()) {
            throw new IllegalArgumentException("Customer profile already exists.");
        }
        Customer customer = customerRepository.save(Customer.builder()
                .user(user)
                .mobile(customerDto.getMobile())
                .gender(customerDto.getGender())
                .address(customerDto.getAddress())
                .dob(customerDto.getDob())
                .kyc_status(false)
                .build());
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto approveKyc(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found."));
        customer.setKyc_status(true);
        return modelMapper.map(customerRepository.save(customer), CustomerDto.class);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Authentication is required.");
        }
        return user;
    }
}
