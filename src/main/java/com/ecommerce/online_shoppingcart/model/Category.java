package com.ecommerce.online_shoppingcart.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity(name = "categories")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @NotBlank
    @Size(min = 5, message = "category name must contains atleast 5 characters")
    private String categoryName;
    private String description;
    @Version
    private Long version;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    public List<Product> products;

    public Category(long l, String category1, String description1) {
    }
}