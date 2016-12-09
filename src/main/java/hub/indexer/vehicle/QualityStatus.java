package de.mobile.inventorylistindexer.indexer.vehicle;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
public enum QualityStatus {

    DELETED("DELETED"),
    BLOCKED("BLOCKED");
    private final String value;
    private final static Map<String, QualityStatus> CONSTANTS = new HashMap<String, QualityStatus>();

    static {
        for (QualityStatus c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private QualityStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static QualityStatus fromValue(String value) {
        QualityStatus constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
