package hub.kafka;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import hub.config.Config;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

@Module
public class KafkaModule {

    @Provides @Singleton
    TopicConnectionFactory provideTopicConnectionFactory() {
        return new TopicConnectionFactory(Config.mustExist("kafka.databroker.host"));
    }

    @Provides @Singleton
    KafkaProducer producer() {
        Properties props = new Properties();
        props.put("client.id", "dealership-profile-service");
        props.put("bootstrap.servers", Config.mustExist("kafka.databroker.host"));
        props.put("acks", "all");
        props.put("retries", java.lang.Integer.valueOf(32));
        props.put("compression.type", "lz4");

        return new KafkaProducer(
                props,
                new org.apache.kafka.common.serialization.StringSerializer(),
                new org.apache.kafka.common.serialization.StringSerializer()
        );
    }
}
