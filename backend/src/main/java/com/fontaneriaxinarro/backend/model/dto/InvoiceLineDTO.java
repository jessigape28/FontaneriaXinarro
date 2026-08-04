package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;

import com.fontaneriaxinarro.backend.model.enums.IvaType;

public record InvoiceLineDTO(
    Long id,
    Long idItem,
    String itemName,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    IvaType ivaType,
    BigDecimal subtotal
) {}
