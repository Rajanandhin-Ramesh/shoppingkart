package com.ecommerce.online_shoppingcart.service;


import com.ecommerce.online_shoppingcart.payload.ProductDTO;
import com.ecommerce.online_shoppingcart.payload.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy,String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,String sortOrder);

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductDTO deleteProduct(Long productId);
}
