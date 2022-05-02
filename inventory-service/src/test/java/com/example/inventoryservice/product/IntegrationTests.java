package com.example.inventoryservice.product;

import com.example.inventoryservice.product.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationTests {
    @Autowired
    private WebTestClient webClient;

    @Container
    public static GenericContainer mysqlContainer = new GenericContainer("quay.io/debezium/example-mysql:1.9")
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "debezium")
            .withEnv("MYSQL_USER", "mysqluser")
            .withEnv("MYSQL_PASSWORD", "mysqlpw");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", IntegrationTests::getMySQLUrl);
    }

    @Test
    void canGetProduct() {
        webClient
                .get()
                .uri("/product/108")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(108)
                .jsonPath("$.name").isEqualTo("jacket")
                .jsonPath("$.description").isEqualTo("water resistent black wind breaker")
                .jsonPath("$.weight").isEqualTo(0.1D);
    }

    @Test
    void canCreateProduct() {
        ProductDTO product = new ProductDTO(null,
                "Test product",
                "Yeah nah",
                0.11D);

        webClient
                .post()
                .uri("/product")
                .body(Mono.just(product), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Test product")
                .jsonPath("$.description").isEqualTo("Yeah nah")
                .jsonPath("$.weight").isEqualTo(0.11D);
    }

    private static String getMySQLUrl() {
        return "jdbc:mysql://localhost:" + mysqlContainer.getMappedPort(3306) + "/inventory";
    }
}
