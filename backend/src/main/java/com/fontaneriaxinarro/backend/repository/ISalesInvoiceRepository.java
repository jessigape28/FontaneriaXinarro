package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.SalesInvoice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;


public interface ISalesInvoiceRepository extends JpaRepository <SalesInvoice, Long> {

    Optional<SalesInvoice> findByNumber(String number);

    List<SalesInvoice> findByCustomerId(Long customerId);

    List<SalesInvoice> findByStatus(InvoiceStatus status);

    List<SalesInvoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
}
