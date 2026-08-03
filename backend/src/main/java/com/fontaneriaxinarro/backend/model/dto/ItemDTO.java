package com.fontaneriaxinarro.backend.model.dto;

import java.math.BigDecimal;

public record ItemDTO(
    Long id,
    String name,
    String brand,
    String model,
    BigDecimal price

) {}
