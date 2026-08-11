package com.fontaneriaxinarro.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.CreateInvoiceLineRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.CreateSalesInvoiceRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.SalesInvoiceDTO;
import com.fontaneriaxinarro.backend.model.dto.SalesInvoiceSummaryDTO;
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
import com.fontaneriaxinarro.backend.security.CurrentUserProvider;




@Service
public class SalesInvoiceService {

    private final ISalesInvoiceRepository salesInvoiceRepository;
    private final ICustomerRepository customerRepository;
    private final IItemRepository itemRepository;
    private final CurrentUserProvider currentUserProvider;

    public SalesInvoiceService(ISalesInvoiceRepository salesInvoiceRepository,
                                ICustomerRepository customerRepository,
                                IItemRepository itemRepository,
                                CurrentUserProvider currentUserProvider) {
        
        this.salesInvoiceRepository = salesInvoiceRepository;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public List<SalesInvoiceSummaryDTO> findAll() {
        return salesInvoiceRepository.findAll().stream()
                .map(this::convertToSummaryDTO)
                .toList();
    }

    public SalesInvoiceDTO findById(Long id) {
        SalesInvoice salesInvoice = salesInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalesInvoice not found: " + id));

            return convertToDTO(salesInvoice);    
    }

    public SalesInvoiceDTO createDraft(CreateSalesInvoiceRequestDTO request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));

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
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + lineRequest.itemId()));
            
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

        salesInvoice.setCreatedBy(currentUserProvider.getCurrentUser());

        SalesInvoice savedInvoice = salesInvoiceRepository.save(salesInvoice);

        return convertToDTO(savedInvoice);
    }  
    
    public void deleteSalesInvoice(Long id) {
        SalesInvoice salesInvoice = salesInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalesInvoice not found: " + id));

        if (salesInvoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalArgumentException("Only invoices in DRAFT status can be deleted");
        }

        salesInvoiceRepository.deleteById(id);
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
            salesInvoice.getCustomer().getSurname(),
            lineDTOs,
            paymentDTOs,
            salesInvoice.getCreatedBy().getId(),
            salesInvoice.getCreatedBy().getUsername()
        );
    }

    private SalesInvoiceSummaryDTO convertToSummaryDTO(SalesInvoice salesInvoice) {
        return new SalesInvoiceSummaryDTO(
            salesInvoice.getId(),
            salesInvoice.getNumber(),
            salesInvoice.getIssueDate(),
            salesInvoice.getTotal(),
            salesInvoice.getStatus(),
            salesInvoice.getCustomer().getId(),
            salesInvoice.getCustomer().getName(),
            salesInvoice.getCustomer().getSurname(),
            salesInvoice.getCreatedBy().getId(),
            salesInvoice.getCreatedBy().getUsername()
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
