package com.example.inventorycdc.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Product {

    private Integer id;
    private String name;
    private String description;
    private Double weight;

}
