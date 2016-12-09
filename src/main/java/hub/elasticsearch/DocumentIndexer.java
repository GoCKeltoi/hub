package de.mobile.inventorylistindexer.elasticsearch;

public interface DocumentIndexer<T> {

    void indexDocument(String index, String id, T doc);

    void deleteDocument(String index, String id);
}
