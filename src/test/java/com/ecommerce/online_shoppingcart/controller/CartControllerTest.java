package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.model.Cart;
import com.ecommerce.online_shoppingcart.payload.CartDTO;
import com.ecommerce.online_shoppingcart.repository.CartRepository;
import com.ecommerce.online_shoppingcart.service.CartService;
import com.ecommerce.online_shoppingcart.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private Cart cart;
    @Mock
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToCart() {
        Long productId = 1L;
        Integer quantity = 2;
        CartDTO cartDTO = new CartDTO();
        when(cartService.addProductToCart(productId, quantity)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.addProductToCart(productId, quantity);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void testGetCarts() {
        List<CartDTO> cartDTOs = Collections.singletonList(new CartDTO());
        when(cartService.getAllCarts()).thenReturn(cartDTOs);

        ResponseEntity<List<CartDTO>> response = cartController.getCarts();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(cartDTOs, response.getBody());
    }

    @Test
    void testGetCartById() {
        String emailId = "test@example.com";
        when(authUtil.loggedInEmail()).thenReturn(emailId);
        Long cartId = 1L;
        when(cart.getCartId()).thenReturn(cartId);
        when(cartService.getCart(emailId, cartId)).thenReturn(new CartDTO());
        when(cartRepository.findCartByEmail(emailId)).thenReturn(cart); // Mock the repository method

        ResponseEntity<CartDTO> response = cartController.getCartById();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateCartProduct() {
        Long productId = 1L;
        String operation = "add"; // Can also be "delete"
        CartDTO cartDTO = new CartDTO();
        when(cartService.updateProductQuantityInCart(productId, 1)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.updateCartProduct(productId, operation);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void testDeleteProductFromCart() {
        Long cartId = 1L;
        Long productId = 1L;
        String expectedStatus = "Product deleted successfully";
        when(cartService.deleteProductFromCart(cartId, productId)).thenReturn(expectedStatus);

        ResponseEntity<String> response = cartController.deleteProductFromCart(cartId, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStatus, response.getBody());
    }
}
