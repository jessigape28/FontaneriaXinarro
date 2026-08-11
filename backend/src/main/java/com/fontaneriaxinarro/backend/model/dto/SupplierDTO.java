package com.fontaneriaxinarro.backend.model.dto;

import com.fontaneriaxinarro.backend.model.enums.PaymentOption;
import com.fontaneriaxinarro.backend.model.enums.Province;

public record SupplierDTO(
    Long id,
    String name,
    String cif,
    String address,
    String postalCode,
    Province province,
    PaymentOption paymentOption,
    Long createdById,
    String createdByUsername
) {}
