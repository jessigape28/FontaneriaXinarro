package com.fontaneriaxinarro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.SupplierDTO;
import com.fontaneriaxinarro.backend.model.entity.Supplier;
import com.fontaneriaxinarro.backend.repository.IPurchaseInvoiceRepository;
import com.fontaneriaxinarro.backend.repository.ISupplierRepository;
import com.fontaneriaxinarro.backend.security.CurrentUserProvider;

@Service
public class SupplierService {

    private final ISupplierRepository supplierRepository;
    private final CurrentUserProvider currentUserProvider;
    private final IPurchaseInvoiceRepository purchaseInvoiceRepository;

    public SupplierService(ISupplierRepository supplierRepository, CurrentUserProvider currentUserProvider,
            IPurchaseInvoiceRepository purchaseInvoiceRepository) {
        this.supplierRepository = supplierRepository;
        this.currentUserProvider = currentUserProvider;
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
    }

    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public SupplierDTO findById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Supplier not found: " + id));

        return convertToDTO(supplier);
    }

    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        supplierRepository.findByCif(supplierDTO.cif())
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con el CIF " + supplierDTO.cif());
                });

        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.name());
        supplier.setCif(supplierDTO.cif());
        supplier.setAddress(supplierDTO.address());
        supplier.setPostalCode(supplierDTO.postalCode());
        supplier.setProvince(supplierDTO.province());
        supplier.setPaymentOption(supplierDTO.paymentOption());

        supplier.setCreatedBy(currentUserProvider.getCurrentUser());

        Supplier savedSupplier = supplierRepository.save(supplier);

        return convertToDTO(savedSupplier);
    }

    public boolean deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found: " + id);

        }
        if (purchaseInvoiceRepository.existsBySupplierId(id)) {
            throw new IllegalArgumentException("No se puede borrar un proveedor que tiene facturas asociadas.");
        }
        supplierRepository.deleteById(id);

        return true;
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found " + id));

        supplierRepository.findByCif(supplierDTO.cif())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con el CIF " + supplierDTO.cif());
                });

        supplier.setName(supplierDTO.name());
        supplier.setCif(supplierDTO.cif());
        supplier.setAddress(supplierDTO.address());
        supplier.setPostalCode(supplierDTO.postalCode());
        supplier.setProvince(supplierDTO.province());
        supplier.setPaymentOption(supplierDTO.paymentOption());

        return convertToDTO(supplierRepository.save(supplier));
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getCif(),
                supplier.getAddress(),
                supplier.getPostalCode(),
                supplier.getProvince(),
                supplier.getPaymentOption(),
                supplier.getCreatedBy().getId(),
                supplier.getCreatedBy().getUsername());
    }

}
