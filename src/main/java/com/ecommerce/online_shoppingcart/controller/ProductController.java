package com.ecommerce.online_shoppingcart.controller;


import com.ecommerce.online_shoppingcart.config.AppConstant;
import com.ecommerce.online_shoppingcart.payload.ProductDTO;
import com.ecommerce.online_shoppingcart.payload.ProductResponse;
import com.ecommerce.online_shoppingcart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product/add")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);

    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(value = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return  new ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(value = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                 @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/search/{keyword}")
    public ResponseEntity<ProductResponse> getPRoductsByKeyword(@PathVariable String keyword,
                                                                 @RequestParam(value = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @PutMapping("/admin/products/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                   @PathVariable Long productId) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);

    }
    @DeleteMapping("/admin/products/delete/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deleteProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deleteProduct, HttpStatus.OK);

    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }


}
