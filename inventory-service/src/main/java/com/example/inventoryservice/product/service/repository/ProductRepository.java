package com.example.inventoryservice.product.service.repository;

import com.example.inventoryservice.product.service.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
