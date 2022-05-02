package com.example.inventoryservice.product.dto;

import com.example.inventoryservice.product.service.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private Double weight;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.weight = product.getWeight();
    }
}
