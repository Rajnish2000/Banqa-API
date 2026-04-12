package com.rajnish.bankmanagementRESTAPI.repositories;

import com.rajnish.bankmanagementRESTAPI.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
     @Query("SELECT c FROM Customer c WHERE c.user.id = :userId")
     Optional<Customer> findCustomerByUserId(@Param("userId") Long userId);
}
