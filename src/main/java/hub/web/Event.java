package hub.web;


import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressFBWarnings
@Entity(value = "event", noClassnameStored = true)
public class Event {

    @Id
    private ObjectId id;

    private Date creationTime;

    private final Map<String, String> delegate = Maps.newHashMap();

    private Map<String, String> values = Maps.newHashMap();

    private Event() {
        // used by morphia
    }
    public Event(String application, String category, String event) {
        this.creationTime = new Date();
    }

    public void addValue(String key, String value) {
        if (null == values) {
            values = Maps.newHashMap();
        }
        values.put(key, value);
    }

    public ObjectId getId() {
        return id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Map<String, String> getValues() {
        return null != values ? values : new HashMap<String, String>();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
