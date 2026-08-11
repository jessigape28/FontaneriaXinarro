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

import com.fontaneriaxinarro.backend.model.dto.CreatePurchaseInvoiceRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.PurchaseInvoiceDTO;
import com.fontaneriaxinarro.backend.model.dto.PurchaseInvoiceSummaryDTO;
import com.fontaneriaxinarro.backend.service.PurchaseInvoiceService;

@RestController
@RequestMapping("/api/purchase-invoices")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService purchaseInvoiceService;

    public PurchaseInvoiceController(PurchaseInvoiceService purchaseInvoiceService) {
        this.purchaseInvoiceService = purchaseInvoiceService;
    }

    @GetMapping
    public List<PurchaseInvoiceSummaryDTO> findAll() {
        return purchaseInvoiceService.findAll();
    }

    @GetMapping("/{id}")
    public PurchaseInvoiceDTO findById(@PathVariable Long id) {
        return purchaseInvoiceService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseInvoiceDTO> createPurchaseInvoice(@RequestBody CreatePurchaseInvoiceRequestDTO request) {
        PurchaseInvoiceDTO createdPurchaseInvoice = purchaseInvoiceService.createPurchaseInvoice(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchaseInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseInvoice(@PathVariable Long id) {
        purchaseInvoiceService.deletePurchaseInvoice(id);

        return ResponseEntity.noContent().build();
    }

}
