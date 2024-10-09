package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.exception.APIException;
import com.ecommerce.online_shoppingcart.exception.ResourceNotFoundException;
import com.ecommerce.online_shoppingcart.model.Category;
import com.ecommerce.online_shoppingcart.payload.CategoryDTO;
import com.ecommerce.online_shoppingcart.payload.CategoryResponse;
import com.ecommerce.online_shoppingcart.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{

//    private List<Category> categories = new ArrayList<>();
    @Autowired
    private CategoryRepository categoryRepository;
//    private  Long nextId = 1L;

    @Autowired
    private ModelMapper modelMapper;
    public CategoryServiceImpl(ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);



        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No category created till now");
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category,CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategories(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategoryFromDB != null)
            throw new APIException("Category with the name " + category.getCategoryName() + " already exist !!!");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


    @Override
    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategoryFromDB = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Manually set fields from DTO to existing category (excluding id, version, etc.)
        savedCategoryFromDB.setCategoryName(categoryDTO.getCategoryName());
        savedCategoryFromDB.setDescription(categoryDTO.getDescription());
        // Continue setting other fields if necessary

        // Save the updated entity
        Category updatedCategory = categoryRepository.save(savedCategoryFromDB);

        // Map the saved entity back to DTO
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override

    public CategoryDTO deleteCategory(Long categoryId) {
                    Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

}
