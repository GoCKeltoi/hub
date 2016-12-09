package de.mobile.inventorylistindexer.kafka;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopicConnectionFactory {
    private static final Logger logger = LoggerFactory.getLogger(TopicConnectionFactory.class);

    private final String bootstrapServers;

    public TopicConnectionFactory(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public TopicConnection fromBeginningClient(String kafkaGroupId) {
        return client(kafkaGroupId, "earliest");
    }

    public TopicConnection fromEndClient(String kafkaGroupId) {
        return client(kafkaGroupId, "latest");
    }

    public TopicConnection resumingClient(String kafkaGroupId) {
        return client(kafkaGroupId, "none");
    }

    private TopicConnection client(String kafkaGroupId, String autoOffsetReset) {
        logger.info("Creating new Kafka client with group id: {}", kafkaGroupId);
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", kafkaGroupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", autoOffsetReset);

        KafkaConsumer client = new KafkaConsumer<>(props);
        client.subscribe(Collections.singletonList("mobile-ad-log"));

        return new TopicConnectionImpl<>(kafkaGroupId, client);
    }

}
