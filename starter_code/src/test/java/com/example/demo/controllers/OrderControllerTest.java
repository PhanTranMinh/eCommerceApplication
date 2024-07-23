package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);
    }

    @Test
    public void submitOrder() {
        User userEmulator = getUser();
        when(userRepository.findByUsername("userTest")).thenReturn(userEmulator);

        ResponseEntity<UserOrder> response = orderController.submit("userTest");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void submitOrderWithUserNotExists() {
        when(userRepository.findByUsername("userTest")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("userTest");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() {
        User userEmulator = getUser();
        when(userRepository.findByUsername("userTest")).thenReturn(userEmulator);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("userTest");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void getOrdersForUserWithUserNotExists() {
        when(userRepository.findByUsername("userTest")).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("userTest");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private User getUser() {
        Item item = getItem();
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("userTest");
        user.setPassword("Hashed");
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(Collections.singletonList(item));
        BigDecimal total = BigDecimal.valueOf(10);
        cart.setTotal(total);
        user.setCart(cart);
        return user;
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
