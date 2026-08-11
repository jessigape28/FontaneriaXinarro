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

import com.fontaneriaxinarro.backend.model.dto.CreateSalesInvoiceRequestDTO;
import com.fontaneriaxinarro.backend.model.dto.SalesInvoiceDTO;
import com.fontaneriaxinarro.backend.model.dto.SalesInvoiceSummaryDTO;
import com.fontaneriaxinarro.backend.service.SalesInvoiceService;

@RestController
@RequestMapping("/api/sales-invoices")
public class SalesInvoiceController {

    private final SalesInvoiceService salesInvoiceService;

    public SalesInvoiceController(SalesInvoiceService salesInvoiceService) {
        this.salesInvoiceService = salesInvoiceService;
    }

    @GetMapping
    public List<SalesInvoiceSummaryDTO> findAll() {
        return salesInvoiceService.findAll();
    }

    @GetMapping("/{id}")
    public SalesInvoiceDTO findById(@PathVariable Long id) {
        return salesInvoiceService.findById(id);
    }

    @PostMapping
    public ResponseEntity<SalesInvoiceDTO> createSalesInvoice(@RequestBody CreateSalesInvoiceRequestDTO request) {
        SalesInvoiceDTO createdSalesInvoice = salesInvoiceService.createDraft(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSalesInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesInvoice(@PathVariable Long id) {
        salesInvoiceService.deleteSalesInvoice(id);

        return ResponseEntity.noContent().build();
    }
}
