package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.exception.APIException;
import com.ecommerce.online_shoppingcart.exception.ResourceNotFoundException;
import com.ecommerce.online_shoppingcart.model.Category;
import com.ecommerce.online_shoppingcart.payload.CategoryDTO;
import com.ecommerce.online_shoppingcart.payload.CategoryResponse;
import com.ecommerce.online_shoppingcart.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories_NoCategories() {
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        APIException exception = assertThrows(APIException.class, () -> {
            categoryService.getAllCategories(0, 10, "categoryName", "asc");
        });

        assertEquals("No category created till now", exception.getMessage());
    }

    @Test
    public void testGetAllCategories_Success() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Category1", "Description1"));
        categories.add(new Category(2L, "Category2", "Description2"));

        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(categories));
        when(modelMapper.map(any(Category.class), eq(CategoryDTO.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            return new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getDescription());
        });

        CategoryResponse response = categoryService.getAllCategories(0, 10, "categoryName", "asc");
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
    }

//    @Test
//    public void testCreateCategories_AlreadyExists() {
//        // Arrange
//        CategoryDTO categoryDTO = new CategoryDTO(1L, "Category1", "Description");
//        Category existingCategory = new Category(1L, "Category1", "Description");
//
//        // Mock the repository to return an existing category
//        when(categoryRepository.findByCategoryName("Category1")).thenReturn(existingCategory);
//        // Mock the modelMapper to map the DTO to the entity
//        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(existingCategory);
//
//        // Act & Assert
//        APIException exception = assertThrows(APIException.class, () -> {
//            categoryService.createCategories(categoryDTO);
//        });
//
//        // Assert the exception message
//        assertEquals("Category with the name Category1 already exist !!!", exception.getMessage());
//    }

    @Test
    public void testUpdateCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO(0L, "UpdatedCategory", "UpdatedDescription");
        Category existingCategory = new Category(1L, "Category1", "Description1");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);
        when(modelMapper.map(existingCategory, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, 1L);
        assertEquals("UpdatedCategory", updatedCategory.getCategoryName());
    }

    @Test
    public void testDeleteCategory_Success() {
        Category category = new Category(1L, "Category1", "Description1");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(new CategoryDTO(1L, "Category1", "Description1"));

        CategoryDTO deletedCategory = categoryService.deleteCategory(1L);
        assertEquals(1L, deletedCategory.getCategoryId());
        verify(categoryRepository, times(1)).delete(category);
    }
    @Test
    public void testCreateCategories_AlreadyExists() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("ExistingCategory");

        // Mock the repository to return an existing category when the findByCategoryName method is called
        when(categoryRepository.findByCategoryName("ExistingCategory"))
                .thenReturn(new Category(1L, "ExistingCategory", "Some description"));

        // Assert that the service throws an APIException
        APIException exception = assertThrows(APIException.class, () -> {
            categoryService.createCategories(categoryDTO);
        });

//        assertEquals("Category with the name ExistingCategory already exists !!!", exception.getMessage());
    }
}
