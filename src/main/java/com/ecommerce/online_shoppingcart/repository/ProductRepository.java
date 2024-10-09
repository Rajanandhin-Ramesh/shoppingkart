package com.ecommerce.online_shoppingcart.repository;

import com.ecommerce.online_shoppingcart.model.Category;
import com.ecommerce.online_shoppingcart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductNameLikeIgnoreCase(String s, Pageable pageDetails);

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);
}
