package com.fontaneriaxinarro.backend.model.dto;

import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.CustomerType;
import com.fontaneriaxinarro.backend.model.enums.IvaType;

public record CustomerDTO(
    Long id,
    String name,
    String surname,
    String dniCif,
    String email,
    String phone,
    CustomerType type, 
    IvaType ivaType,
    String notes,
    String bankAccount,
    Long propertyAdministratorId,
    String propertyAdministratorName,
    List<AddressDTO> addresses
) {}


