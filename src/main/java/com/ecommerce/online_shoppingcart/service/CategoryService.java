package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.payload.CategoryDTO;
import com.ecommerce.online_shoppingcart.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String order);
    CategoryDTO createCategories(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

    CategoryDTO deleteCategory(Long categoryId);
}
