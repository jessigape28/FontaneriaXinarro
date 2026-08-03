package com.fontaneriaxinarro.backend.model.dto;

import com.fontaneriaxinarro.backend.model.enums.Province;

public record AddressDTO(
    Long id,
    String alias,
    String street,
    String postalCode,
    Province province
) {

}
