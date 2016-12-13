package hub.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TopicConnectionImpl<K, V> implements TopicConnection {

    private static final Logger logger = LoggerFactory.getLogger(TopicConnectionImpl.class);

    private final String groupId;
    private KafkaConsumer<K, V> client;

    public TopicConnectionImpl(String groupId, KafkaConsumer<K, V> client) {
        this.groupId = groupId;
        this.client = client;
        Runtime.getRuntime().addShutdownHook(
                new Thread(this::shutdownHook, "kafka-consumer-" + groupId + "-shutdown"));
    }

    @Override
    public ConsumerRecords<K, V> poll(long timeout) {
        return client.poll(timeout);
    }

    @Override
    public void commitSync() {
        client.commitSync();
    }

    @Override
    public void shutdown() {
        try {
            logger.info("Shutting down connection with group id: {}", groupId);
            client.unsubscribe();
            // do not sync on shutdown, because we don't know how many messages we consumed
            // before we got the WakeupException
            client.close();
        } finally {
            client = null;
        }
    }

    @Override
    public void moveToEndOfTopic() {
        // force assignment of partitions to us
        client.poll(0);

        logger.debug("Before moving to end of topic:");
        logCurrentPositions();

        client.seekToEnd(client.assignment());
        for (TopicPartition tp : client.assignment()) {

            // force kafka to update positions on the server side, since seekToEnd() is a lazy call
            client.position(tp);
        }

        client.commitSync();
        logger.debug("After moving to end of topic:");
        logCurrentPositions();

    }

    @Override
    public void logCurrentPositions() {
        if (!logger.isDebugEnabled()) {
            return;
        }

        for (TopicPartition tp : client.assignment()) {
            logger.debug("Current position: {} for {} with group id: {}", client.position(tp), tp, groupId);
        }
    }

    private void shutdownHook() {
        if (client != null) {
            logger.info("Waking up kafka consumer with group id: {}", groupId);
            client.wakeup();
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
