package hub.indexer;


import static com.codahale.metrics.MetricRegistry.name;

import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;

import rx.Observable;

import hub.elasticsearch.DocumentIndexer;
import hub.kafka.EventConsumer;

import java.lang.reflect.Type;
import java.util.Map;


class VehicleEventConsumer implements EventConsumer<ConsumerRecord<String, String>> {


    private final Logger logger = LoggerFactory.getLogger(VehicleEventConsumer.class);

    private final DocumentIndexer<Map<String, Object>> documentIndexer;

    private final Gson gson;

    private final MetricRegistry mr;

    VehicleEventConsumer(
            DocumentIndexer<Map<String, Object>> documentIndexer,
            Gson gson,
            MetricRegistry mr
    ) {
        this.documentIndexer = documentIndexer;
        this.gson = gson;
        this.mr = mr;
    }

    @Override
    public void accept(final String index, ConsumerRecord<String, String> record) {
        logger.info("process event: {}", record);
        try {

            if (isDeleteEvent(record)) {
                documentIndexer.deleteDocument(index, record.key());
            } else {

                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                documentIndexer.indexDocument(index, record.key(), gson.fromJson(record.value(), type));
            }

        } catch (Exception e) {
            mr.counter(name("outbound", "es", "error")).inc();
            logger.error("error to process a message: {}", record, e);
        }
    }

    private static boolean isDeleteEvent(ConsumerRecord<String, String> record){
        return null == record.value() || record.value().length() <=0;
    }
}
