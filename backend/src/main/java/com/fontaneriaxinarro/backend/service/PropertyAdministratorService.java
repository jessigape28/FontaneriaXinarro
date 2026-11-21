package com.fontaneriaxinarro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.PropertyAdministratorDTO;
import com.fontaneriaxinarro.backend.model.entity.PropertyAdministrator;
import com.fontaneriaxinarro.backend.repository.ICustomerRepository;
import com.fontaneriaxinarro.backend.repository.IPropertyAdministratorRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyAdministratorService {

    private final IPropertyAdministratorRepository propertyAdministratorRepository;
    private final ICustomerRepository customerRepository;

    public PropertyAdministratorService(IPropertyAdministratorRepository propertyAdministratorRepository,
            ICustomerRepository customerRepository) {
        this.propertyAdministratorRepository = propertyAdministratorRepository;
        this.customerRepository = customerRepository;
    }

    public List<PropertyAdministratorDTO> findAll() {
        return propertyAdministratorRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public PropertyAdministratorDTO findById(Long id) {
        PropertyAdministrator propertyAdministrator = propertyAdministratorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PropertyAdministrator not found: " + id));

        return convertToDTO(propertyAdministrator);
    }

    public PropertyAdministratorDTO createPropertyAdministrator(PropertyAdministratorDTO propertyAdministratorDTO) {
        PropertyAdministrator propertyAdministrator = new PropertyAdministrator();
        propertyAdministrator.setName(propertyAdministratorDTO.name());
        propertyAdministrator.setEmail(propertyAdministratorDTO.email());
        propertyAdministrator.setPhone(propertyAdministratorDTO.phone());

        PropertyAdministrator savedPropertyAdministrator = propertyAdministratorRepository.save(propertyAdministrator);

        return convertToDTO(savedPropertyAdministrator);

    }

    public boolean deletePropertyAdministrator(Long id) {
        if (!propertyAdministratorRepository.existsById(id)) {
            throw new ResourceNotFoundException("PropertyAdministrator not found: " + id);

        }
        if (customerRepository.existsByPropertyAdministratorId(id)) {
            throw new IllegalArgumentException(
                    "No se puede borrar un Administrador de fincas que tiene clientes asociados.");
        }
        propertyAdministratorRepository.deleteById(id);

        return true;
    }

    @Transactional
    public PropertyAdministratorDTO updatePropertyAdministrator(Long id, PropertyAdministratorDTO propAdminDTO) {
        PropertyAdministrator propertyAdministrator = propertyAdministratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property Administrator not found: " + id));

        propertyAdministrator.setName(propAdminDTO.name());
        propertyAdministrator.setEmail(propAdminDTO.email());
        propertyAdministrator.setPhone(propAdminDTO.phone());

        return convertToDTO(propertyAdministratorRepository.save(propertyAdministrator));
    }

    private PropertyAdministratorDTO convertToDTO(PropertyAdministrator propertyAdministrator) {
        return new PropertyAdministratorDTO(
                propertyAdministrator.getId(),
                propertyAdministrator.getName(),
                propertyAdministrator.getEmail(),
                propertyAdministrator.getPhone());
    }
}
