package com.fontaneriaxinarro.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.PaymentTerm;

public record CreatePurchaseInvoiceRequestDTO(
    Long supplierId,
    String number,
    LocalDate issueDate,
    PaymentTerm paymentTerm,
    String notes,
    List<CreateInvoiceLineRequestDTO> lines
) {

}
