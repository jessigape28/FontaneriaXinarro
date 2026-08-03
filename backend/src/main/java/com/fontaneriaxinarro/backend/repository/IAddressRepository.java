package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.Address;

public interface IAddressRepository extends JpaRepository <Address, Long> {

}
