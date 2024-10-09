package com.ecommerce.online_shoppingcart.repository;

import com.ecommerce.online_shoppingcart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}
