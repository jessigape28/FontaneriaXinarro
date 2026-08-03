package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.PropertyAdministrator;



public interface IPropertyAdministratorRepository extends JpaRepository <PropertyAdministrator, Long> {

    PropertyAdministrator findByEmail(String email);
}
