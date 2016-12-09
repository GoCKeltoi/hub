package de.mobile.inventorylistindexer.kafka;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.config.Config;

@Module
public class KafkaModule {

    @Provides @Singleton
    TopicConnectionFactory provideTopicConnectionFactory() {
        return new TopicConnectionFactory(Config.mustExist("kafka.databroker.host"));
    }

}
