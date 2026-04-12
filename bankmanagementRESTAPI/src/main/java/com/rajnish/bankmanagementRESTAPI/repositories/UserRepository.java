package com.rajnish.bankmanagementRESTAPI.repositories;

import com.rajnish.bankmanagementRESTAPI.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}