package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.Customer;

import java.util.List;
import java.util.Optional;



public interface ICustomerRepository extends JpaRepository<Customer, Long>{

    Optional<Customer> findByDniCif(String dniCif);


    List<Customer> findByEmail(String email);

    List<Customer> findByNameContainingIgnoreCase(String name);

    List<Customer>findBySurnameContainingIgnoreCase(String surname);

    List<Customer> findByAddresses_StreetContainingIgnoreCase(String street);
} 


