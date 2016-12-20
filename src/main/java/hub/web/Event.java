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

    private Map<String, Object> values = Maps.newHashMap();

    private Event() {
        // used by morphia
    }
    public Event(Map<String, Object> values) {
        this.creationTime = new Date();
        this.values = values;
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

    public Map<String, Object> getValues() {
        return null != values ? values : new HashMap<String, Object>();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
