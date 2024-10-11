package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.exception.APIException;
import com.ecommerce.online_shoppingcart.exception.ResourceNotFoundException;
import com.ecommerce.online_shoppingcart.model.Cart;
import com.ecommerce.online_shoppingcart.model.CartItem;
import com.ecommerce.online_shoppingcart.model.Product;
import com.ecommerce.online_shoppingcart.payload.CartDTO;
import com.ecommerce.online_shoppingcart.repository.CartItemRepository;
import com.ecommerce.online_shoppingcart.repository.CartRepository;
import com.ecommerce.online_shoppingcart.repository.ProductRepository;
import com.ecommerce.online_shoppingcart.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ModelMapper modelMapper;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(0.0);
        cart.setCartItems(new ArrayList<>());

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setQuantity(10);
        product.setSpecialPrice(100.0);
    }

    @Test
    void addProductToCart_Success() {
        when(authUtil.loggedInEmail()).thenReturn("test@example.com");
        when(cartRepository.findCartByEmail("test@example.com")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(null);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(new CartDTO());

        CartDTO result = cartService.addProductToCart(product.getProductId(), 1);

        assertNotNull(result);
        assertEquals(0, cart.getCartItems().size());
    }

    @Test
    void addProductToCart_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.addProductToCart(1L, 1);
        });

        assertEquals("Product not found with productId: 1", exception.getMessage());
    }

    @Test
    void addProductToCart_ProductAlreadyInCart() {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cart.getCartItems().add(cartItem);

        when(authUtil.loggedInEmail()).thenReturn("test@example.com");
        when(cartRepository.findCartByEmail("test@example.com")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(cartItem);

        Exception exception = assertThrows(APIException.class, () -> {
            cartService.addProductToCart(product.getProductId(), 1);
        });

        assertEquals("Product Test Product already exists in the cart", exception.getMessage());
    }

    @Test
    void updateProductQuantityInCart_Success() {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cart.getCartItems().add(cartItem);

        when(authUtil.loggedInEmail()).thenReturn("test@example.com");
        when(cartRepository.findCartByEmail("test@example.com")).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(cartItem);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(new CartDTO());

        CartDTO result = cartService.updateProductQuantityInCart(product.getProductId(), 1);

        assertNotNull(result);
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void updateProductQuantityInCart_ProductNotFound() {
        Long productId = 1L;
        Integer quantity = 1;

        // Mock the behavior of authUtil to return a valid email
        String email = "test@example.com";
        when(authUtil.loggedInEmail()).thenReturn(email);

        // Mock the cart repository to return a cart associated with the user
        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(100.0);
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);

        // Mock the product repository to return null for the product
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Assert that the exception is thrown for product not found
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateProductQuantityInCart(productId, quantity);
        });

        assertEquals("Cart not found with cartId: 1", exception.getMessage()); // Adjust this based on your expected message
    }

    @Test
    void deleteProductFromCart_Success() {
        Long cartId = 1L;
        Long productId = 1L;

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setTotalPrice(100.0);


        CartItem cartItem = new CartItem();
        cartItem.setProductPrice(50.0);
        cartItem.setQuantity(2);
        cartItem.setProduct(new Product("Samsung21","default.png","Samsung's flagship model with advanced camera and performance.",30,999.99,5.0,949.99));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId)).thenReturn(cartItem);

        String result = cartService.deleteProductFromCart(cartId, productId);

        assertEquals("Product Samsung21 removed from the cart !!!", result);
        assertEquals(0.0, cart.getTotalPrice()); // Assuming it was completely removed
    }

    @Test
    void deleteProductFromCart_ProductNotFound() {
        Long cartId = 1L;
        Long productId = 1L;

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setTotalPrice(100.0);

        // Mock the cart repository to return the cart
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // Mock the cart item repository to return null for the specified productId
        when(cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId)).thenReturn(null);

        // Assert that the exception is thrown for the product not found
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.deleteProductFromCart(cartId, productId);
        });

        assertEquals("Product not found with productId: " + productId, exception.getMessage());
    }


    @Test
    void getAllCarts_NoCarts() {
        when(cartRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(APIException.class, () -> {
            cartService.getAllCarts();
        });

        assertEquals("No cart exists", exception.getMessage());
    }

    @Test
    void getAllCarts_Success() {
        List<Cart> carts = new ArrayList<>();
        carts.add(cart);
        when(cartRepository.findAll()).thenReturn(carts);
        when(modelMapper.map(any(Cart.class), any())).thenReturn(new CartDTO());

        List<CartDTO> result = cartService.getAllCarts();

        assertEquals(1, result.size());
    }

    @Test
    void getCart_NotFound() {
        when(cartRepository.findCartByEmailAndCartId(anyString(), anyLong())).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCart("test@example.com", 1L);
        });

        assertEquals("Cart not found with cartId: 1", exception.getMessage());
    }

    @Test
    void getCart_Success() {
        when(cartRepository.findCartByEmailAndCartId(anyString(), anyLong())).thenReturn(cart);
        when(modelMapper.map(any(Cart.class), any())).thenReturn(new CartDTO());

        CartDTO result = cartService.getCart("test@example.com", 1L);

        assertNotNull(result);
    }
}
