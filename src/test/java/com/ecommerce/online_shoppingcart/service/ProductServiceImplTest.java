package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.exception.APIException;
import com.ecommerce.online_shoppingcart.exception.ResourceNotFoundException;
import com.ecommerce.online_shoppingcart.model.Cart;
import com.ecommerce.online_shoppingcart.model.Category;
import com.ecommerce.online_shoppingcart.model.Product;
import com.ecommerce.online_shoppingcart.payload.CartDTO;
import com.ecommerce.online_shoppingcart.payload.ProductDTO;
import com.ecommerce.online_shoppingcart.payload.ProductResponse;
import com.ecommerce.online_shoppingcart.repository.CartRepository;
import com.ecommerce.online_shoppingcart.repository.CategoryRepository;
import com.ecommerce.online_shoppingcart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductDTO productDTO;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        
        productDTO = new ProductDTO();
        productDTO.setProductName("Laptop");
        productDTO.setPrice(1000.0);
        productDTO.setDiscount(10.0);
        
        product = new Product();
        product.setProductId(1L);
        product.setProductName("Laptop");
        product.setPrice(1000.0);
        product.setDiscount(10.0);
        product.setSpecialPrice(900.0);
        product.setCategory(category);
    }

    @Test
    void testAddProduct_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductDTO result = productService.addProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProduct_ProductAlreadyExists() {
        List<Product> existingProducts = new ArrayList<>();
        existingProducts.add(product);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        category.setProducts(existingProducts);

        APIException exception = assertThrows(APIException.class, () -> {
            productService.addProduct(1L, productDTO);
        });

        assertEquals("Product already exist", exception.getMessage());
    }

    @Test
    void testGetAllProducts_NoProducts() {
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        APIException exception = assertThrows(APIException.class, () -> {
            productService.getAllProducts(0, 10, "price", "asc");
        });

        assertEquals("No product Exist till now!!!", exception.getMessage());
    }

    @Test
    void testGetAllProducts_Success() {
        List<Product> products = Collections.singletonList(product);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(products));
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response = productService.getAllProducts(0, 10, "price", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Laptop", response.getContent().get(0).getProductName());
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductDTO result = productService.updateProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductName());
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(1L, productDTO);
        });

        assertEquals("Product", exception.getResourceName());
        assertEquals("productId", exception.getFieldName());
        assertEquals(1L, exception.getField());
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDTO result = productService.deleteProduct(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductName());
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    void testDeleteProduct_NotFound() {
        Long nonExistentProductId = 999L; // Use an ID that doesn't exist in the database

        // Mock the productRepository to return an empty Optional
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // Verify that the ResourceNotFoundException is thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(nonExistentProductId);
        });

        assertEquals("Product", exception.getResourceName());
        assertEquals("productId", exception.getFieldName());
        assertEquals(nonExistentProductId, exception.getField());
    }

}
