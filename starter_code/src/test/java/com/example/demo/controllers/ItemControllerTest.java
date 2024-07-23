package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems() {
        List<Item> items = new ArrayList<>();
        Item itemEmulator = getItem();
        items.add(itemEmulator);
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> returnItem = response.getBody();
        assertNotNull(returnItem);
    }

    @Test
    public void getItemById() {
        Item itemEmulator = getItem();
        when(itemRepository.findById(0L)).thenReturn(Optional.of((itemEmulator)));
        final ResponseEntity<Item> response = itemController.getItemById(0L);
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(itemEmulator.getId(), item.getId());
    }

    @Test
    public void getItemByIdNotExists() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByName() {
        List<Item> items = new ArrayList<>();
        Item itemEmulator = getItem();
        items.add(itemEmulator);
        when(itemRepository.findByName(itemEmulator.getName())).thenReturn(items);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemEmulator.getName());
        List<Item> itemResponse = response.getBody();
        assertNotNull(itemResponse);
        assertEquals(itemResponse.get(0).getName(), itemEmulator.getName());
    }

    @Test
    public void getItemsByNameNotExists() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testNotExist");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = BigDecimal.valueOf(10);
        item.setPrice(price);
        item.setDescription("Description item 1");
        return item;
    }
}
