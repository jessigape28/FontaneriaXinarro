package com.fontaneriaxinarro.backend.model.dto;

import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.PaymentTerm;

public record CreateSalesInvoiceRequestDTO(
    Long customerId,
    PaymentTerm paymentTerm,
    String notes,
    List<CreateInvoiceLineRequestDTO> invoiceLines
) {

}
