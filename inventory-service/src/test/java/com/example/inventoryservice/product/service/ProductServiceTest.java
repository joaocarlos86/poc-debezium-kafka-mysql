package com.example.inventoryservice.product.service;

import com.example.inventoryservice.product.dto.ProductDTO;
import com.example.inventoryservice.product.service.model.Product;
import com.example.inventoryservice.product.service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductService.class)
class ProductServiceTest {

    @MockBean
    private ProductRepository repository;

    @Autowired
    private ProductService service;

    @Test
    void getProduct_givenProductExist_shouldReturnProduct() {
        when(repository.findById(eq(108))).thenReturn(Optional.of(new Product()));

        Optional<Product> product = service.getProduct(108);
        assertThat(product.isPresent()).isTrue();
    }

    @Test
    void getProduct_givenProductDoesntExist_shouldReturnEmpty() {
        when(repository.findById(eq(108))).thenReturn(Optional.empty());

        Optional<Product> product = service.getProduct(108);
        assertThat(product.isEmpty()).isTrue();
    }

    @Test
    void createProduct_shouldCreateProduct() {
        ProductDTO productDTO = new ProductDTO(null, "p name", "p desc", 0.11D);
        Product product = new Product(null, "p name", "p desc", 0.11D);
        Product created = new Product(1, "p name", "p desc", 0.11D);
        when(repository.save(product)).thenReturn(created);

        Product result = service.createProduct(productDTO);
        assertThat(result).isEqualTo(created);
    }

}