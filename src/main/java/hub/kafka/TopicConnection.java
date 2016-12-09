package de.mobile.inventorylistindexer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface TopicConnection<K, V> extends AutoCloseable {

    ConsumerRecords<K, V> poll(long timeout);

    void commitSync();

    void shutdown();

    void moveToEndOfTopic();

    void logCurrentPositions();

    @Override
    void close();

}
