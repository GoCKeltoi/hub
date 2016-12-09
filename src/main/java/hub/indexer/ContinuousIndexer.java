package de.mobile.inventorylistindexer.indexer;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

import de.mobile.inventorylistindexer.config.Config;
import de.mobile.inventorylistindexer.elasticsearch.EsAliasResolver;
import de.mobile.inventorylistindexer.elasticsearch.EsAliasSamePredicate;
import de.mobile.inventorylistindexer.kafka.KafkaTopicReader;
import de.mobile.inventorylistindexer.kafka.TopicConnectionFactory;
import de.mobile.inventorylistindexer.util.Resolver;

public class ContinuousIndexer {

    private static final Logger logger = LoggerFactory.getLogger(ContinuousIndexer.class);

    private final EsAliasResolver aliasResolver;
    private final TopicConnectionFactory tcf;
    private final VehicleEventConsumer vec;
    private final MetricRegistry mr;

    public ContinuousIndexer(EsAliasResolver aliasResolver, TopicConnectionFactory tcf, VehicleEventConsumer vec, MetricRegistry mr) {
        this.aliasResolver = aliasResolver;
        this.tcf = tcf;
        this.vec = vec;
        this.mr = mr;
    }

    public void start() {
        while (true) {
            try {
                final String index = Resolver.withRetry(aliasResolver, Duration.ofSeconds(Config.get("aliasResolverRetry", 60)));
                Thread.currentThread().setName("realtime-indexer-" + index);
                final EsAliasSamePredicate aliasSame = new EsAliasSamePredicate(aliasResolver);

                final KafkaTopicReader<ConsumerRecord> topic = new KafkaTopicReader<>(tcf, mr);

                topic.consume(index, aliasSame, vec);
            } catch (WakeupException ex) {
                break;
            }
        }

    }

}
