package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.CustomerDto;
import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.CustomerRepository;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CustomerDto addCustomerDetails(Long id, CustomerDto customerDto) {
        User user = userRepository.findById(id).orElse(null);
        Customer customer = customerRepository.save(Customer.builder()
                .user(user).mobile(customerDto.getMobile())
                .gender(customerDto.getGender())
                .address(customerDto.getAddress())
                .dob(customerDto.getDob())
                .kyc_status(customerDto.getKyc_status())
                .build()
        );
        return modelMapper.map(customer, CustomerDto.class);
    }
}
