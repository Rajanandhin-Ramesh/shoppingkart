package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.config.AppConstant;
import com.ecommerce.online_shoppingcart.model.Category;
import com.ecommerce.online_shoppingcart.payload.CategoryDTO;
import com.ecommerce.online_shoppingcart.payload.CategoryResponse;
import com.ecommerce.online_shoppingcart.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_CATEGORIES_BY,required = false) String sortBy,
                                                             @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }
    @PostMapping("/public/categories/add")
//    @RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    public ResponseEntity<CategoryDTO> createCategories(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedcategoryDTO = categoryService.createCategories(categoryDTO);
        return  new ResponseEntity<>(savedcategoryDTO,HttpStatus.CREATED);

    }

    @PutMapping("/public/categories/update/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);

    }

    @DeleteMapping("/admin/categories/delete/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
            CategoryDTO deleteCategory = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deleteCategory, HttpStatus.OK);
    }
}
