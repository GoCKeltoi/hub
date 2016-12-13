package hub.indexer;


import static com.codahale.metrics.MetricRegistry.name;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;

import rx.Observable;

import hub.elasticsearch.DocumentIndexer;
import hub.kafka.EventConsumer;


class VehicleEventConsumer implements EventConsumer<ConsumerRecord<String, String>> {

    private static final Status DEFAULT_STATUS = new Status();
    private static final Observable<Status> STATUS_ON_ERROR = Observable.just(DEFAULT_STATUS);
    private static final Observable<VehicleImages> IMAGES_ON_ERROR = Observable.just(VehicleImages.EMPTY);

    private final Logger logger = LoggerFactory.getLogger(VehicleEventConsumer.class);

    private final DocumentIndexer<VehicleESDoc> documentIndexer;
    private final OrderApi orderApi;
    private final InventoryApi inventoryApi;
    private final Gson gson;

    private final MetricRegistry mr;

    VehicleEventConsumer(
            DocumentIndexer<VehicleESDoc> documentIndexer,
            OrderApi orderApi,
            InventoryApi inventoryApi,
            Gson gson,
            MetricRegistry mr
    ) {
        this.documentIndexer = documentIndexer;
        this.orderApi = orderApi;
        this.inventoryApi = inventoryApi;
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

                final Vehicle vehicle = gson.fromJson(record.value(), Vehicle.class);

                VehicleESDoc doc = buildESDocument(vehicle);
                documentIndexer.indexDocument(index, doc.getVehicleId(), doc);
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
