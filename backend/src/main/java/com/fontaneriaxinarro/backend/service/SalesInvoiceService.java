package com.fontaneriaxinarro.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.model.dto.CreateInvoiceLineRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.CreateSalesInvoiceRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.SalesInvoiceDTO;
import com.fontaneriaxinarro.backend.model.dto.InvoiceLineDTO;
import com.fontaneriaxinarro.backend.model.dto.InvoicePaymentDTO;
import com.fontaneriaxinarro.backend.model.entity.Customer;
import com.fontaneriaxinarro.backend.model.entity.Item;
import com.fontaneriaxinarro.backend.model.entity.SalesInvoice;
import com.fontaneriaxinarro.backend.model.entity.SalesInvoiceLine;
import com.fontaneriaxinarro.backend.model.entity.SalesInvoicePayment;
import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;
import com.fontaneriaxinarro.backend.repository.ICustomerRepository;
import com.fontaneriaxinarro.backend.repository.IItemRepository;
import com.fontaneriaxinarro.backend.repository.ISalesInvoiceRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SalesInvoiceService {

    private final ISalesInvoiceRepository salesInvoiceRepository;
    private final ICustomerRepository customerRepository;
    private final IItemRepository itemRepository;

    public SalesInvoiceService(ISalesInvoiceRepository salesInvoiceRepository,
                                ICustomerRepository customerRepository,
                                IItemRepository itemRepository) {
        
        this.salesInvoiceRepository = salesInvoiceRepository;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
    }

    public List<SalesInvoiceDTO> findAll() {
        return salesInvoiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public SalesInvoiceDTO findById(Long id) {
        SalesInvoice salesInvoice = salesInvoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SalesInvoice not found: " + id));

            return convertToDTO(salesInvoice);    
    }

    public SalesInvoiceDTO createDraft(CreateSalesInvoiceRequestDTO request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + request.customerId()));

        SalesInvoice salesInvoice = new SalesInvoice();
        salesInvoice.setCustomer(customer);
        salesInvoice.setPaymentTerm(request.paymentTerm());
        salesInvoice.setNotes(request.notes());
        salesInvoice.setStatus(InvoiceStatus.DRAFT);
        salesInvoice.setCreatedAt(LocalDate.now());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaAmount = BigDecimal.ZERO;

        for (CreateInvoiceLineRequestDTO lineRequest : request.invoiceLines()) {
            Item item = itemRepository.findById(lineRequest.itemId())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found: " + lineRequest.itemId()));
            
            SalesInvoiceLine line = new SalesInvoiceLine();
            line.setSalesInvoice(salesInvoice);
            line.setItem(item);
            line.setDescription(lineRequest.description() != null ? lineRequest.description() : item.getName());
            line.setQuantity(lineRequest.quantity());
            BigDecimal unitPrice = lineRequest.unitPrice() != null ? lineRequest.unitPrice() : item.getPrice();
            line.setUnitPrice(unitPrice);
            line.setIvaType(lineRequest.ivaType());

            BigDecimal lineSubtotal = unitPrice
                .multiply(BigDecimal.valueOf(lineRequest.quantity()))
                .setScale(2, RoundingMode.HALF_UP);
            line.setSubtotal(lineSubtotal);

            salesInvoice.getLines().add(line);

            BigDecimal lineIva = lineSubtotal
                .multiply(lineRequest.ivaType().getPorcentaje())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(lineSubtotal);
            ivaAmount = ivaAmount.add(lineIva);
            
        }

        salesInvoice.setSubtotal(subtotal);
        salesInvoice.setIvaAmount(ivaAmount);
        salesInvoice.setTotal(subtotal.add(ivaAmount));

        SalesInvoice savedInvoice = salesInvoiceRepository.save(salesInvoice);

        return convertToDTO(savedInvoice);
    }    
    
    
    private SalesInvoiceDTO convertToDTO(SalesInvoice salesInvoice) {
        List<InvoiceLineDTO> lineDTOs = salesInvoice.getLines().stream()
                .map(this::convertLineToDTO)
                .toList();
        
        List<InvoicePaymentDTO> paymentDTOs = salesInvoice.getPayments().stream()
                .map(this::convertPaymentToDTO)
                .toList();

        return new SalesInvoiceDTO(
            salesInvoice.getId(),
            salesInvoice.getNumber(),
            salesInvoice.getCreatedAt(),
            salesInvoice.getIssueDate(),
            salesInvoice.getInvoiceFilePath(),
            salesInvoice.getPaymentTerm(),
            salesInvoice.getSubtotal(),
            salesInvoice.getIvaAmount(),
            salesInvoice.getTotal(),
            salesInvoice.getStatus(),
            salesInvoice.getNotes(),
            salesInvoice.getCustomer().getId(),
            salesInvoice.getCustomer().getName(),
            lineDTOs,
            paymentDTOs
            
        );
    }

    private InvoiceLineDTO convertLineToDTO(SalesInvoiceLine line) {
        return new InvoiceLineDTO(
            line.getId(),
            line.getItem() != null ? line.getItem().getId() : null,
            line.getItem() != null ? line.getItem().getName() : null,
            line.getDescription(),
            line.getQuantity(),
            line.getUnitPrice(),
            line.getIvaType(),
            line.getSubtotal()
        );
    }

    private InvoicePaymentDTO convertPaymentToDTO(SalesInvoicePayment payment) {
        return new InvoicePaymentDTO(
            payment.getId(),
            payment.getDueDate(),
            payment.getAmount(),
            payment.isPaid(),
            payment.getPaymentDate(),
            payment.getReceiptFilePath()
        );
    }
    
}
