package de.mobile.inventorylistindexer.kafka;

import java.util.function.BiConsumer;

public interface EventConsumer<V> extends BiConsumer<String, V> {
}
