package com.fontaneriaxinarro.backend.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fontaneriaxinarro.backend.model.enums.InvoiceStatus;
import com.fontaneriaxinarro.backend.model.enums.PaymentTerm;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "invoice_file_path")
    private String invoiceFilePath;

    @Enumerated(EnumType.STRING)
    private PaymentTerm paymentTerm;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "iva_amount", nullable = false)
    private BigDecimal ivaAmount;
    
    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

}
