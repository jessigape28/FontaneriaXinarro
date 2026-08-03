package com.fontaneriaxinarro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fontaneriaxinarro.backend.model.dto.ItemDTO;
import com.fontaneriaxinarro.backend.model.entity.Item;
import com.fontaneriaxinarro.backend.repository.IItemRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ItemService {

    private final IItemRepository itemRepository;
    
    public ItemService(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDTO> findAll() {
        return itemRepository.findAll().stream()
            .map(this::convertToDTO)
            .toList();
    }

    public ItemDTO findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Item not found: " + id));
        
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
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);

            return true;
        } else {
            throw new EntityNotFoundException("Item not found: " + id);
        }
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

}
