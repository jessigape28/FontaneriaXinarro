package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;

public record PurchaseInvoiceSummaryDTO(
    Long id,
    String number,
    LocalDate issueDate,
    BigDecimal total,
    InvoiceStatus status,
    Long supplierId,
    String supplierName,
    Long createdById,
    String createdByUsername
) {}
