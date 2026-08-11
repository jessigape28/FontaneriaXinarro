package com.fontaneriaxinarro.backend.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.fontaneriaxinarro.backend.model.enums.CustomerType;
import com.fontaneriaxinarro.backend.model.enums.IvaType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String surname;

    @Column(nullable = false, unique = true)
    private String dniCif;;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "iva_type", nullable = false)
    private IvaType ivaType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "bank_account")
    private String bankAccount;

    @ManyToOne
    @JoinColumn(name = "property_administrator_id")
    private PropertyAdministrator propertyAdministrator;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
