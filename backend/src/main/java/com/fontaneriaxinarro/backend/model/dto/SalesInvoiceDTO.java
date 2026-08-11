package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;
import com.fontaneriaxinarro.backend.model.enums.PaymentTerm;

public record SalesInvoiceDTO(
    Long id,
    String number,
    LocalDate createdAt,
    LocalDate issueDate,
    String invoiceFilePath,
    PaymentTerm paymentTerm,
    BigDecimal subtotal,
    BigDecimal ivaAmount,
    BigDecimal total, 
    InvoiceStatus status,
    String notes,
    Long customerId,
    String customerName,
    String customerSurname,
    List<InvoiceLineDTO> lines,
    List<InvoicePaymentDTO> payments,
    Long createdById,
    String createdByUsername
) {}
