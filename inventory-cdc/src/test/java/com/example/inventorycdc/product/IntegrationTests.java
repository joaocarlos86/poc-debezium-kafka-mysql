package com.example.inventorycdc.product;

import com.example.inventorycdc.product.consumer.ProductChangeConsumer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Log4j2
public class IntegrationTests {
    @Container
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("kafka", 9093, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30L)))
                    .withExposedService("mysql", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30L)))
                    .withExposedService("connect", 8083, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30L)));

    @Autowired
    private ProductChangeConsumer consumer;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", IntegrationTests::getKafkaURL);
    }

    private static String getKafkaURL() {
        return "localhost:" + environment.getServicePort("kafka", 9093);
    }

    @Test
    void test() throws IOException, InterruptedException {
        final Boolean registered = registerMySQLConnectorToKafkaConnect();
        assertThat(registered).isTrue().withFailMessage("Couldn't register connector to Kafka Connector");

        assertThat(consumer.getTotalMessagesProcessed()).isEqualTo(9);
    }

    private Boolean registerMySQLConnectorToKafkaConnect() throws InterruptedException {
        final String requestJson = """
                {
                  "name": "inventory-connector",
                  "config": {
                    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
                    "tasks.max": "1",
                    "database.hostname": "mysql",
                    "database.port": "3306",
                    "database.user": "debezium",
                    "database.password": "dbz",
                    "database.server.id": "184054",
                    "database.server.name": "dbserver1",
                    "database.include.list": "inventory",
                    "database.history.kafka.bootstrap.servers": "kafka:9092",
                    "database.history.kafka.topic": "dbhistory.inventory"
                  }
                }
                """;

        final RestTemplate restTemplate = new RestTemplate();
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        final HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);
        final String url = "http://localhost:" + environment.getServicePort("connect", 8083) + "/connectors/";
        final ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        TimeUnit.SECONDS.sleep(2);

        return response.getStatusCode().is2xxSuccessful();
    }
}
