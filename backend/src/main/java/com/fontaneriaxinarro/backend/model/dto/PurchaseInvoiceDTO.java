package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;
import com.fontaneriaxinarro.backend.model.enums.PaymentTerm;

public record PurchaseInvoiceDTO(
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
    Long supplierId,
    String supplierName,
    List<InvoicePaymentDTO> payments
) {

}
