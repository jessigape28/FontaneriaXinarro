package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;

import com.fontaneriaxinarro.backend.model.enums.IvaType;

public record CreateInvoiceLineRequestDTO(
    Long itemId,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    IvaType ivaType
) {}
