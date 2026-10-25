package com.fontaneriaxinarro.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.dto.ItemDTO;
import com.fontaneriaxinarro.backend.model.entity.Item;
import com.fontaneriaxinarro.backend.repository.IItemRepository;
import com.fontaneriaxinarro.backend.repository.IPurchaseInvoiceLineRepository;
import com.fontaneriaxinarro.backend.repository.ISalesInvoiceLineRepository;




@Service
public class ItemService {

    private final IItemRepository itemRepository;
    private final ISalesInvoiceLineRepository salesInvoiceLineRepository;
    private final IPurchaseInvoiceLineRepository purchaseInvoiceLineRepository;
    
    public ItemService(IItemRepository itemRepository,
                        ISalesInvoiceLineRepository salesInvoiceLineRepository,
                        IPurchaseInvoiceLineRepository purchaseInvoiceLineRepository
    ) {
        this.itemRepository = itemRepository;
        this.salesInvoiceLineRepository = salesInvoiceLineRepository;
        this.purchaseInvoiceLineRepository = purchaseInvoiceLineRepository;
    }

    public List<ItemDTO> findAll() {
        return itemRepository.findAll().stream()
            .map(this::convertToDTO)
            .toList();
    }

    public ItemDTO findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Item not found: " + id));
        
        return convertToDTO(item);
    }
    
    public ItemDTO createItem(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.name());
        item.setBrand(itemDTO.brand());
        item.setModel(itemDTO.model());
        item.setPrice(itemDTO.price());

        Item savedItem = itemRepository.save(item);

        return convertToDTO(savedItem);
    }

    public boolean deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found: " + id);
            
        } 
        if (salesInvoiceLineRepository.existsByItemId(id) || purchaseInvoiceLineRepository.existsByItemId(id)) {
            throw new IllegalArgumentException("No se puede borrar un artículo que aparece en facturas");
        }

        itemRepository.deleteById(id);

        return true;
    }

    private ItemDTO convertToDTO(Item item) {
        return new ItemDTO(
            item.getId(),
            item.getName(),
            item.getBrand(),
            item.getModel(),
            item.getPrice()
        );
    }

    @Transactional 
    public ItemDTO updateItem(Long id, ItemDTO itemDTO) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        
            validatePrice(itemDTO.price());

            item.setName(itemDTO.name());
            item.setBrand(itemDTO.brand());
            item.setModel(itemDTO.model());
            item.setPrice(itemDTO.price());

            return convertToDTO(itemRepository.save(item));
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio es obligatorio y no puede ser negativo");
        }
    }

}
