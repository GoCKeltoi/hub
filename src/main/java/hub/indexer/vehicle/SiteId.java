package de.mobile.inventorylistindexer.indexer.vehicle;

import java.util.HashMap;
import java.util.Map;

public enum SiteId {

    GERMANY("GERMANY"),
    FRANCE("FRANCE"),
    ITALY("ITALY");
    private final String value;
    private final static Map<String, SiteId> CONSTANTS = new HashMap<String, SiteId>();

    static {
        for (SiteId c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private SiteId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static SiteId fromValue(String value) {
        SiteId constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
