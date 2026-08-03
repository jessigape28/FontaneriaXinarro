package com.fontaneriaxinarro.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.SalesInvoicePayment;

public interface ISalesInvoicePaymentRepository extends JpaRepository<SalesInvoicePayment, Long>{

}
