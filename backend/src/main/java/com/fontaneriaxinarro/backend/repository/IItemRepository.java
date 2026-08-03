package com.fontaneriaxinarro.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fontaneriaxinarro.backend.model.entity.Item;

public interface IItemRepository extends JpaRepository <Item, Long> {

    List<Item> findByNameContainingIgnoreCase(String name);

}
