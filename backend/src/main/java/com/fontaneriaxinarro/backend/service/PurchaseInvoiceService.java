package com.fontaneriaxinarro.backend.service;

import com.fontaneriaxinarro.backend.model.dto.CreateInvoiceLineRequestDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.model.dto.CreatePurchaseInvoiceRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.PurchaseInvoiceDTO;
import com.fontaneriaxinarro.backend.model.dto.InvoicePaymentDTO;
import com.fontaneriaxinarro.backend.model.entity.Item;
import com.fontaneriaxinarro.backend.model.entity.PurchaseInvoice;
import com.fontaneriaxinarro.backend.model.entity.PurchaseInvoiceLine;
import com.fontaneriaxinarro.backend.model.entity.PurchaseInvoicePayment;
import com.fontaneriaxinarro.backend.model.entity.Supplier;
import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;
import com.fontaneriaxinarro.backend.repository.IItemRepository;
import com.fontaneriaxinarro.backend.repository.IPurchaseInvoiceRepository;
import com.fontaneriaxinarro.backend.repository.ISupplierRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PurchaseInvoiceService {

    private final IPurchaseInvoiceRepository purchaseInvoiceRepository;
    private final ISupplierRepository supplierRepository;
    private final IItemRepository itemRepository;
    
    public PurchaseInvoiceService(IPurchaseInvoiceRepository purchaseInvoiceRepository,
                                ISupplierRepository supplierRepository, 
                                IItemRepository itemRepository) {

        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierRepository = supplierRepository;
        this.itemRepository = itemRepository;
    }

    public List<PurchaseInvoiceDTO> findAll() {
        return purchaseInvoiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public PurchaseInvoiceDTO findById(Long id) {
        PurchaseInvoice purchaseInvoice = purchaseInvoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PurchaseInvoice not found: " + id));

            return convertToDTO(purchaseInvoice);        
    }

    public PurchaseInvoiceDTO create(CreatePurchaseInvoiceRequestDTO request) {

        Supplier supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + request.supplierId()));
        
        PurchaseInvoice purchaseInvoice = new PurchaseInvoice();
        purchaseInvoice.setSupplier(supplier);
        purchaseInvoice.setNumber(request.number());
        purchaseInvoice.setIssueDate(request.issueDate());
        purchaseInvoice.setPaymentTerm(request.paymentTerm());
        purchaseInvoice.setNotes(request.notes());
        purchaseInvoice.setStatus(InvoiceStatus.PENDING);
        purchaseInvoice.setCreatedAt(LocalDate.now());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaAmount = BigDecimal.ZERO;

        for (CreateInvoiceLineRequestDTO lineRequest : request.lines()) {
            Item item = itemRepository.findById(lineRequest.itemId())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found: " + lineRequest.itemId()));

            PurchaseInvoiceLine line = new PurchaseInvoiceLine();
            line.setPurchaseInvoice(purchaseInvoice);
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

            purchaseInvoice.getLines().add(line);

            BigDecimal lineIva = lineSubtotal
                        .multiply(lineRequest.ivaType().getPorcentaje())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            subtotal = subtotal.add(lineSubtotal);
            ivaAmount = ivaAmount.add(lineIva);

        }

        purchaseInvoice.setSubtotal(subtotal);
        purchaseInvoice.setIvaAmount(ivaAmount);
        purchaseInvoice.setTotal(subtotal.add(ivaAmount));

        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(purchaseInvoice);

        return convertToDTO(savedInvoice);

    }

    

    private PurchaseInvoiceDTO convertToDTO(PurchaseInvoice purchaseInvoice) {
        List<InvoicePaymentDTO> paymentsDTOs = purchaseInvoice.getPayments().stream()
                .map(this::convertPaymentToDTO)
                .toList();

        return new PurchaseInvoiceDTO(
            purchaseInvoice.getId(),
            purchaseInvoice.getNumber(),
            purchaseInvoice.getCreatedAt(),
            purchaseInvoice.getIssueDate(),
            purchaseInvoice.getInvoiceFilePath(),
            purchaseInvoice.getPaymentTerm(),
            purchaseInvoice.getSubtotal(),
            purchaseInvoice.getIvaAmount(),
            purchaseInvoice.getTotal(),
            purchaseInvoice.getStatus(),
            purchaseInvoice.getNotes(),
            purchaseInvoice.getSupplier().getId(),
            purchaseInvoice.getSupplier().getName(),
            paymentsDTOs
        );
    }

    private InvoicePaymentDTO convertPaymentToDTO(PurchaseInvoicePayment payment) {
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
