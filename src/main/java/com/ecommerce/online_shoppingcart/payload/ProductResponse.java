package com.ecommerce.online_shoppingcart.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductResponse {
    private List<ProductDTO> content;
    private  Integer pageNumber;
    private  Integer pageSize;
    private  Long totalElements;
    private  Integer totalPages;
    private  boolean lastPage;

    public ProductResponse() {

    }
}
