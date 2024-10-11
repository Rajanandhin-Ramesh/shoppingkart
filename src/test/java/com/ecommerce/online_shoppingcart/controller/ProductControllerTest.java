package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.config.AppConstant;
import com.ecommerce.online_shoppingcart.payload.ProductDTO;
import com.ecommerce.online_shoppingcart.payload.ProductResponse;
import com.ecommerce.online_shoppingcart.service.ProductService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private MultipartFile image;  // Mock for MultipartFile if needed

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct() {
        ProductDTO productDTO = new ProductDTO();
        ProductDTO savedProductDTO = new ProductDTO();
        when(productService.addProduct(anyLong(), any(ProductDTO.class))).thenReturn(savedProductDTO);

        ResponseEntity<ProductDTO> response = productController.addProduct(productDTO, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedProductDTO, response.getBody());
    }

    @Test
    void testGetAllProducts() {
        ProductResponse productResponse = new ProductResponse();
        when(productService.getAllProducts(anyInt(), anyInt(), any(), any())).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getAllProducts(0, 10, "name", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void testGetProductsByCategory() {
        ProductResponse productResponse = new ProductResponse();
        when(productService.searchByCategory(anyLong(), anyInt(), anyInt(), any(), any())).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProductsByCategory(1L, 0, 10, "name", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void testGetProductsByKeyword() {
        ProductResponse productResponse = new ProductResponse();
        when(productService.searchProductByKeyword(any(), anyInt(), anyInt(), any(), any())).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getPRoductsByKeyword("keyword", 0, 10, "name", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void testUpdateProduct() {
        ProductDTO productDTO = new ProductDTO();
        ProductDTO updatedProductDTO = new ProductDTO();
        when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(updatedProductDTO);

        ResponseEntity<ProductDTO> response = productController.updateProduct(productDTO, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductDTO, response.getBody());
    }

    @Test
    void testDeleteProduct() {
        ProductDTO deletedProductDTO = new ProductDTO();
        when(productService.deleteProduct(anyLong())).thenReturn(deletedProductDTO);

        ResponseEntity<ProductDTO> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deletedProductDTO, response.getBody());
    }

    @Test
    void testUpdateProductImage() throws IOException {
        ProductDTO updatedProductDTO = new ProductDTO();
        when(productService.updateProductImage(anyLong(), any(MultipartFile.class))).thenReturn(updatedProductDTO);

        ResponseEntity<ProductDTO> response = productController.updateProductImage(1L, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductDTO, response.getBody());
    }
}
