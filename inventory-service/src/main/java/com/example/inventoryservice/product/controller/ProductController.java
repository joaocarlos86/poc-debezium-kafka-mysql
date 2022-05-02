package com.example.inventoryservice.product.controller;

import com.example.inventoryservice.product.dto.ProductDTO;
import com.example.inventoryservice.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/product")
public class ProductController {

    private ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDTO>> getProduct(@PathVariable Integer id) {
        return Mono
                .just(service.getProduct(id))
                .map(p -> {
                    if (p.isPresent()) {
                        return ResponseEntity.ok(new ProductDTO(p.get()));
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProductDTO());
                    }
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ProductDTO>> createProduct(@RequestBody final ProductDTO request) {
        return Mono.just(service.createProduct(request))
                .map(p -> ResponseEntity.ok(new ProductDTO(p)));
    }
}
