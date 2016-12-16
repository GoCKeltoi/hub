package hub.web;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class Event extends ForwardingMap<String, String> {

    private final Map<String, String> delegate = Maps.newHashMap();

    @Override
    protected Map<String, String> delegate() {
        return delegate;
    }

    @Override
    public String get(Object key) {
        if (!super.containsKey(key)) {
            throw new IllegalArgumentException(key + " is not defined");
        }
        return super.get(key);
    }
}
