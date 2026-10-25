package com.fontaneriaxinarro.backend.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.AddressDTO;
import com.fontaneriaxinarro.backend.model.dto.CustomerDTO;
import com.fontaneriaxinarro.backend.model.entity.Address;
import com.fontaneriaxinarro.backend.model.entity.Customer;
import com.fontaneriaxinarro.backend.model.entity.PropertyAdministrator;
import com.fontaneriaxinarro.backend.repository.ICustomerRepository;
import com.fontaneriaxinarro.backend.repository.IPropertyAdministratorRepository;
import com.fontaneriaxinarro.backend.repository.ISalesInvoiceRepository;
import com.fontaneriaxinarro.backend.security.CurrentUserProvider;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CurrentUserProvider currentUserProvider;
    private final ICustomerRepository customerRepository;
    private final IPropertyAdministratorRepository propertyAdministratorRepository;
    private final ISalesInvoiceRepository salesInvoiceRepository;

    public CustomerService(ICustomerRepository customerRepository,
            IPropertyAdministratorRepository propertyAdministratorRepository, CurrentUserProvider currentUserProvider,
            ISalesInvoiceRepository salesInvoiceRepository) {
        this.customerRepository = customerRepository;
        this.propertyAdministratorRepository = propertyAdministratorRepository;
        this.currentUserProvider = currentUserProvider;
        this.salesInvoiceRepository = salesInvoiceRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found: " + id));

        return convertToDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        if (customerRepository.findByDniCif(customerDTO.dniCif()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el DNI/CIF " + customerDTO.dniCif());
        }

        Customer customer = new Customer();
        customer.setName(customerDTO.name());
        customer.setSurname(customerDTO.surname());
        customer.setDniCif(customerDTO.dniCif());
        customer.setEmail(customerDTO.email());
        customer.setPhone(customerDTO.phone());
        customer.setType(customerDTO.type());
        customer.setIvaType(customerDTO.ivaType());
        customer.setNotes(customerDTO.notes());
        customer.setBankAccount(customerDTO.bankAccount());

        if (customerDTO.propertyAdministratorId() != null) {
            PropertyAdministrator propertyAdministrator = propertyAdministratorRepository
                    .findById(customerDTO.propertyAdministratorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Property Administrator not found: " + customerDTO.propertyAdministratorId()));

            customer.setPropertyAdministrator(propertyAdministrator);
        }

        customer.setCreatedBy(currentUserProvider.getCurrentUser());

        if (customerDTO.addresses() != null) {
            for (AddressDTO addressDTO : customerDTO.addresses()) {
                Address address = new Address();
                address.setAlias(addressDTO.alias());
                address.setStreet(addressDTO.street());
                address.setTown(addressDTO.town());
                address.setPostalCode(addressDTO.postalCode());
                address.setProvince(addressDTO.province());
                address.setCustomer(customer);
                customer.getAddresses().add(address);
            }
        }

        Customer savedCustomer = customerRepository.save(customer);

        return convertToDTO(savedCustomer);
    }

    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        if (salesInvoiceRepository.existsByCustomerId(id)) {
            throw new IllegalArgumentException("No se puede borrar un cliente que tiene facturas");
        }
        customerRepository.deleteById(id);

        return true;
    }

    private CustomerDTO convertToDTO(Customer customer) {
        List<AddressDTO> addressDTOs = customer.getAddresses().stream()
                .map(this::convertToAddressDTO)
                .toList();

        PropertyAdministrator propertyAdministrator = customer.getPropertyAdministrator();

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getDniCif(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getType(),
                customer.getIvaType(),
                customer.getNotes(),
                customer.getBankAccount(),
                propertyAdministrator != null ? propertyAdministrator.getId() : null,
                propertyAdministrator != null ? propertyAdministrator.getName() : null,
                addressDTOs,
                customer.getCreatedBy().getId(),
                customer.getCreatedBy().getUsername());
    }

    private AddressDTO convertToAddressDTO(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getAlias(),
                address.getStreet(),
                address.getTown(),
                address.getPostalCode(),
                address.getProvince());
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        customerRepository.findByDniCif(customerDTO.dniCif())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Ya existe un cliente con el DNI/CIF " + customerDTO.dniCif());
                });

        customer.setName(customerDTO.name());
        customer.setSurname(customerDTO.surname());
        customer.setDniCif(customerDTO.dniCif());
        customer.setEmail(customerDTO.email());
        customer.setPhone(customerDTO.phone());
        customer.setType(customerDTO.type());
        customer.setIvaType(customerDTO.ivaType());
        customer.setNotes(customerDTO.notes());
        customer.setBankAccount(customerDTO.bankAccount());

        if (customerDTO.propertyAdministratorId() != null) {
            PropertyAdministrator propertyAdministrator = propertyAdministratorRepository
                    .findById(customerDTO.propertyAdministratorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Property Administrator not found: " + customerDTO.propertyAdministratorName()));

            customer.setPropertyAdministrator(propertyAdministrator);
        } else {
            customer.setPropertyAdministrator(null);
        }

        syncAddresses(customer, customerDTO.addresses());

        return convertToDTO(customerRepository.save(customer));
    }

    private void syncAddresses(Customer customer, List<AddressDTO> addressDTOs) {
        List<AddressDTO> incoming = addressDTOs != null ? addressDTOs : List.of();

        Set<Long> incomingIds = incoming.stream()
                .map(AddressDTO::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        customer.getAddresses().removeIf(address -> !incomingIds.contains(address.getId()));

        for (AddressDTO addressDTO : incoming) {
            if (addressDTO.id() == null) {
                Address address = new Address();
                copyAddressFields(address, addressDTO);
                address.setCustomer(customer);
                customer.getAddresses().add(address);
            } else {
                Address existing = customer.getAddresses().stream()
                        .filter(address -> address.getId().equals(addressDTO.id()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressDTO.id()));

                copyAddressFields(existing, addressDTO);
            }
        }
    }

    private void copyAddressFields(Address address, AddressDTO addressDTO) {
        address.setAlias(addressDTO.alias());
        address.setStreet(addressDTO.street());
        address.setTown(addressDTO.town());
        address.setPostalCode(addressDTO.postalCode());
        address.setProvince(addressDTO.province());
    }
}
