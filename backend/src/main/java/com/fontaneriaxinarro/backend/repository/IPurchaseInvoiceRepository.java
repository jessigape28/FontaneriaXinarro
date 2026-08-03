package com.fontaneriaxinarro.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.PurchaseInvoice;
import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;

public interface IPurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long>{

    Optional<PurchaseInvoice> findByNumber(String number);

    List<PurchaseInvoice> findBySupplierId(Long supplierId);

    List<PurchaseInvoice> findByStatus(InvoiceStatus status);

    List<PurchaseInvoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);

}
