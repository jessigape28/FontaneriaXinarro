package com.fontaneriaxinarro.backend.web.restcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fontaneriaxinarro.backend.model.dto.PropertyAdministratorDTO;
import com.fontaneriaxinarro.backend.service.PropertyAdministratorService;

@RestController
@RequestMapping("/api/propertyAdministrators")
public class PropertyAdministratorController {

    private final PropertyAdministratorService propertyAdministratorService;

    public PropertyAdministratorController(PropertyAdministratorService propertyAdministratorService) {
        this.propertyAdministratorService = propertyAdministratorService;
    }

    @GetMapping
    public List<PropertyAdministratorDTO> findAll() {
        return propertyAdministratorService.findAll();
    }

    @GetMapping("{id}")
    public PropertyAdministratorDTO findById(@PathVariable Long id) {
        return propertyAdministratorService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PropertyAdministratorDTO> createPropertyAdministrator(@RequestBody PropertyAdministratorDTO propertyAdministratorDTO) {
        PropertyAdministratorDTO createdPropertyAdministrator = propertyAdministratorService.createPropertyAdministrator(propertyAdministratorDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPropertyAdministrator);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePropertyAdministrator(@PathVariable Long id) {
        propertyAdministratorService.deletePropertyAdministrator(id);

        return ResponseEntity.noContent().build();
    }
    
}
