package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoicePaymentDTO(
    Long id,
    LocalDate dueDate,
    BigDecimal amount,
    boolean paid,
    LocalDate paymentDate,
    String receiptFilePath
) {}
