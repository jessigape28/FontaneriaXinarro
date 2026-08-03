package com.fontaneriaxinarro.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.Supplier;
import java.util.List;


public interface ISupplierRepository extends JpaRepository <Supplier, Long> {

    Optional<Supplier> findByCif(String cif);

    List<Supplier> findByName(String name);


}
