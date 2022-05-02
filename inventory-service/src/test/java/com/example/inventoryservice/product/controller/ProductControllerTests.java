package com.example.inventoryservice.product.controller;

import com.example.inventoryservice.product.dto.ProductDTO;
import com.example.inventoryservice.product.service.model.Product;
import com.example.inventoryservice.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProductController.class)
public class ProductControllerTests {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private ProductService service;

    @Test
    void getProduct_givenProductExists_shouldReturnProduct() {
        Product expected = new Product(108,
                "jacket",
                "water resistent black wind breaker",
                0.1D);

        when(service.getProduct(eq(108))).thenReturn(Optional.of(expected));

        testClient
                .get()
                .uri("/product/108")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class).isEqualTo(new ProductDTO(expected));
    }

    @Test
    void getProduct_givenProductDoesntExists_shouldReturn404() {
        when(service.getProduct(any())).thenReturn(Optional.empty());

        testClient
                .get()
                .uri("/product/108")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createProduct_shouldCreateProduct() {
        ProductDTO createRequest = new ProductDTO(null,
                "another jacket",
                "yeah, water resistent black wind breaker",
                0.111D);

        Product created = new Product(900,
                "another jacket",
                "yeah, water resistent black wind breaker",
                0.111D);

        when(service.createProduct(eq(createRequest))).thenReturn(created);

        testClient
                .post()
                .uri("/product")
                .body(Mono.just(createRequest), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class).isEqualTo(new ProductDTO(created));
    }
}
