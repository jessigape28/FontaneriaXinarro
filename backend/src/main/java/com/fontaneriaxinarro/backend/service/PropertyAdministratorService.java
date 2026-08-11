package com.fontaneriaxinarro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.PropertyAdministratorDTO;
import com.fontaneriaxinarro.backend.model.entity.PropertyAdministrator;
import com.fontaneriaxinarro.backend.repository.IPropertyAdministratorRepository;


@Service
public class PropertyAdministratorService {

    private final IPropertyAdministratorRepository propertyAdministratorRepository;

    public PropertyAdministratorService(IPropertyAdministratorRepository propertyAdministratorRepository) {
        this.propertyAdministratorRepository = propertyAdministratorRepository;
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
        if (propertyAdministratorRepository.existsById(id)) {
            propertyAdministratorRepository.deleteById(id);

            return true;
        } else {
            throw new ResourceNotFoundException("PropertyAdministrator not found: " + id);
        }
    }

    private PropertyAdministratorDTO convertToDTO(PropertyAdministrator propertyAdministrator) {
        return new PropertyAdministratorDTO(
            propertyAdministrator.getId(),
            propertyAdministrator.getName(),
            propertyAdministrator.getEmail(),
            propertyAdministrator.getPhone()
        );
    }
}
