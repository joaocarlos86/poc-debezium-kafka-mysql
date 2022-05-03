package com.example.inventorycdc;

import com.example.inventorycdc.product.model.Product;
import io.debezium.serde.DebeziumSerdes;
import lombok.extern.slf4j.Slf4j;
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

@SpringBootApplication
@Slf4j
public class InventoryCdcApplication {

	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();



	@KafkaListener(id = "customers-consumer-group",
			topicPartitions = {
				@TopicPartition(topic = "dbserver1.inventory.customers", partitions = { "0" },
					partitionOffsets = @PartitionOffset(partition = "*", initialOffset = "0"))}
	)
	public void dltListen(byte[] msg) {
		log.info("Received from DLT: " + new String(msg));
		this.exec.execute(() -> log.info(new String(msg)));
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryCdcApplication.class, args);
	}

}
