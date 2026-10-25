package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.SalesInvoiceLine;

public interface ISalesInvoiceLineRepository extends JpaRepository<SalesInvoiceLine, Long>{

    boolean existsByItemId(Long itemId);
}
