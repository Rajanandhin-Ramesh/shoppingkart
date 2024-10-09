package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.exception.APIException;
import com.ecommerce.online_shoppingcart.payload.CategoryDTO;
import com.ecommerce.online_shoppingcart.payload.CategoryResponse;
import com.ecommerce.online_shoppingcart.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories() {
        CategoryResponse response = new CategoryResponse();
        response.setContent(Collections.emptyList());
        response.setTotalElements(1L);
        response.setTotalPages(0);

        when(categoryService.getAllCategories(0, 10, "categoryName", "asc")).thenReturn(response);

        ResponseEntity<CategoryResponse> result = categoryController.getAllCategories(0, 10, "categoryName", "asc");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    public void testCreateCategories() {
        CategoryDTO categoryDTO = new CategoryDTO(null, "Category1", "Description1");
        when(categoryService.createCategories(categoryDTO)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> result = categoryController.createCategories(categoryDTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Category1", result.getBody().getCategoryName());
    }

    @Test
    public void testUpdateCategory() {
        CategoryDTO categoryDTO = new CategoryDTO(1L, "UpdatedCategory", "UpdatedDescription");
        when(categoryService.updateCategory(categoryDTO, 1L)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> result = categoryController.updateCategory(categoryDTO, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("UpdatedCategory", result.getBody().getCategoryName());
    }

    @Test
    public void testDeleteCategory() {
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Category1", "Description1");
        when(categoryService.deleteCategory(1L)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> result = categoryController.deleteCategory(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getCategoryId());
    }

    @Test
    public void testDeleteCategory_NotFound() {
        when(categoryService.deleteCategory(1L)).thenThrow(new APIException("Category not found"));

        APIException exception = assertThrows(APIException.class, () -> {
            categoryController.deleteCategory(1L);
        });

        assertEquals("Category not found", exception.getMessage());
    }
}
