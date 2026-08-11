package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;

public record SalesInvoiceSummaryDTO(
    Long id,
    String number,
    LocalDate issueDate,
    BigDecimal total,
    InvoiceStatus status,
    Long customerId,
    String customerName,
    String customerSurname,
    Long createdById,
    String createdByUsername
) {}

