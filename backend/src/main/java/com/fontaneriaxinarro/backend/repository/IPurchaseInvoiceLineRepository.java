package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.PurchaseInvoiceLine;

public interface IPurchaseInvoiceLineRepository extends JpaRepository<PurchaseInvoiceLine, Long> {

    boolean existsByItemId(Long itemId);
}
