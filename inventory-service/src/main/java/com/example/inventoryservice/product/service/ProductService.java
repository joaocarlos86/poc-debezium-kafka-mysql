package com.example.inventoryservice.product.service;

import com.example.inventoryservice.product.dto.ProductDTO;
import com.example.inventoryservice.product.service.model.Product;
import com.example.inventoryservice.product.service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Optional<Product> getProduct(Integer id) {
        return repository.findById(id);
    }

    public Product createProduct(ProductDTO dto) {
        Product productToCreate = new Product(null, dto.getName(), dto.getDescription(), dto.getWeight());
        return repository.save(productToCreate);
    }
}
