package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser() throws Exception {
        when(encoder.encode("passwordTest")).thenReturn("Hashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("userTest", user.getUsername());
        assertEquals("Hashed", user.getPassword());
    }

    @Test
    public void testFindUserByUserName() throws Exception {
        User userEmulator = getUser();
        when(userRepository.findByUsername("userTest")).thenReturn(userEmulator);
        final ResponseEntity<User> response = userController.findByUserName("userTest");
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(userEmulator.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByUserNameNotExists() throws Exception {
        when(userRepository.findByUsername("userTest")).thenReturn(null);
        final ResponseEntity<User> response = userController.findByUserName("userTest");
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void testCreateUserNameExists() throws Exception {
        User userEmulator = getUser();
        when(userRepository.findByUsername("userTest")).thenReturn(userEmulator);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("passwordTest");

        thrown.expect(Exception.class);
        thrown.expectMessage("Username is exist");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordNotSame() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest1");

        thrown.expect(Exception.class);
        thrown.expectMessage("Confirm password not mapping");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordLengthNotMin() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");

        thrown.expect(Exception.class);
        thrown.expectMessage("Password less more 6 characters");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testFindUserById() throws Exception {
        User userEmulator = getUser();
        when(userRepository.findById(0L)).thenReturn(Optional.of((userEmulator)));
        final ResponseEntity<User> response = userController.findById(0L);
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(userEmulator.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByIdNotExists() throws Exception {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private User getUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("userTest");
        user.setPassword("passwordTest");
        user.setCart(cart);
        return user;
    }
}
