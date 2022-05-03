package com.example.inventorycdc;

import com.example.inventorycdc.product.model.DebeziumEvent;
import com.example.inventorycdc.product.model.Product;
import io.debezium.serde.DebeziumSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.Map;

@SpringBootApplication
@Slf4j
public class InventoryCdcApplication {

	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();



	@KafkaListener(id = "products-consumer-group",
			topicPartitions = {
				@TopicPartition(topic = "dbserver1.inventory.products", partitions = { "0" },
					partitionOffsets = @PartitionOffset(partition = "*", initialOffset = "0"))}
	)
	public void dltListen(byte[] msg) {
		Serde<DebeziumEvent> productSerde = DebeziumSerdes.payloadJson(DebeziumEvent.class);
		Map<String, ?> configs = Map.of("from.field", "payload", "unknown.properties.ignored", "true");
		productSerde.configure(configs, false);
		Deserializer<DebeziumEvent> des = productSerde.deserializer();
		DebeziumEvent deserialize = des.deserialize("dbserver1.inventory.products", msg);

		log.info("Received from DLT: " + deserialize);
		this.exec.execute(() -> log.info(deserialize.toString()));
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryCdcApplication.class, args);
	}

}
