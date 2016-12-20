package hub.kafka;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.lang.reflect.Type;
import java.util.Map;

public class Publisher {

    private final KafkaProducer producer;
    private static final Type TYPE = new TypeToken<Map<String, Object>>(){}.getType();
    private final Gson gson;

    public Publisher(KafkaProducer producer, Gson gson){
        this.producer = producer;
        this.gson = gson;
    }

    public void changed(Map<String, Object> map) {

        ProducerRecord record = new ProducerRecord(
                "hub_event",
                (String)map.get("id"),
                gson.toJson(map, TYPE)
        );

        producer.send(record);
    }
}
