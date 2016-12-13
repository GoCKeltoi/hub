package hub.kafka;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import hub.config.Config;

@Module
public class KafkaModule {

    @Provides @Singleton
    TopicConnectionFactory provideTopicConnectionFactory() {
        return new TopicConnectionFactory(Config.mustExist("kafka.databroker.host"));
    }

}
