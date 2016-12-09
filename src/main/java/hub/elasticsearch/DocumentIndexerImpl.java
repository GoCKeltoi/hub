package de.mobile.inventorylistindexer.elasticsearch;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;


public class DocumentIndexerImpl<T> implements DocumentIndexer<T> {
    private final Logger logger = LoggerFactory.getLogger(DocumentIndexerImpl.class);

    private final String docType;
    private final Client client;
    private final Gson gson;

    public DocumentIndexerImpl(String docType, Client client, Gson gson) {
        this.docType = docType;
        this.client = client;
        this.gson = gson;
    }

    @Override
    public void indexDocument(String index, String id, T doc) {
        logger.debug("indexing document type: {} id:{}", docType, id);

        client
                .prepareIndex(index, docType, id)
                .setSource(gson.toJson(doc).getBytes(Charsets.UTF_8))
                .execute()
                .actionGet();
    }

    @Override
    public void deleteDocument(String index, String id) {
        logger.debug("deleting document type: {} id:{}", docType, id);

        client
                .prepareIndex(index, docType, id)
                .setSource(ImmutableMap.of("deleted", true))
                .execute()
                .actionGet();
    }

}
