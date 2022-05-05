package com.example.inventorycdc.product.consumer;

import com.example.inventorycdc.product.model.DebeziumEvent;
import io.debezium.serde.DebeziumSerdes;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Log4j2
public class ProductChangeConsumer {
    private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

    private Deserializer<DebeziumEvent> des;
    private AtomicInteger totalMessagesProcessed = new AtomicInteger(0);

    @PostConstruct
    private void init() {
        final Serde<DebeziumEvent> productSerde = DebeziumSerdes.payloadJson(DebeziumEvent.class);
        final Map<String, ?> configs = Map.of("from.field", "payload", "unknown.properties.ignored", "true");
        productSerde.configure(configs, false);

        des = productSerde.deserializer();
    }

    @KafkaListener(id = "products-consumer-group",
            topicPartitions = {
                    @TopicPartition(topic = "dbserver1.inventory.products", partitions = { "0" },
                            partitionOffsets = @PartitionOffset(partition = "*", initialOffset = "0"))}
    )
    public void dltListen(byte[] msg) {
        final DebeziumEvent deserialize = des.deserialize("dbserver1.inventory.products", msg);

        this.exec.execute(() -> {
            log.info(deserialize.toString());
            totalMessagesProcessed.incrementAndGet();
        });
    }

    public Integer getTotalMessagesProcessed() {
        return totalMessagesProcessed.intValue();
    }
}
