package com.fontaneriaxinarro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.model.dto.AddressDTO;
import com.fontaneriaxinarro.backend.model.dto.CustomerDTO;
import com.fontaneriaxinarro.backend.model.entity.Address;
import com.fontaneriaxinarro.backend.model.entity.Customer;
import com.fontaneriaxinarro.backend.model.entity.PropertyAdministrator;
import com.fontaneriaxinarro.backend.repository.ICustomerRepository;
import com.fontaneriaxinarro.backend.repository.IPropertyAdministratorRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

    private final ICustomerRepository customerRepository;
    private final IPropertyAdministratorRepository propertyAdministratorRepository;

    public CustomerService(ICustomerRepository customerRepository, IPropertyAdministratorRepository propertyAdministratorRepository)
    {
        this.customerRepository = customerRepository;
        this.propertyAdministratorRepository = propertyAdministratorRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
            .map(this::convertToDTO)
            .toList();
    }

    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Customer not found: " + id));

        return convertToDTO(customer);    
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
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
            PropertyAdministrator propertyAdministrator = propertyAdministratorRepository.findById(customerDTO.propertyAdministratorId())
                .orElseThrow(() -> new EntityNotFoundException("Property Administrator not found: " + customerDTO.propertyAdministratorId()));

            customer.setPropertyAdministrator(propertyAdministrator);
        }
    
        Customer savedCustomer = customerRepository.save(customer);

        return convertToDTO(savedCustomer);
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);

            return true;
        } else {
            throw new EntityNotFoundException("Customer not foudnd: " + id);
        }
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
            addressDTOs
        );
    }

    private AddressDTO convertToAddressDTO(Address address) {
        return new AddressDTO(
            address.getId(),
            address.getAlias(),
            address.getStreet(),
            address.getPostalCode(),
            address.getProvince()
        );
    }


}
