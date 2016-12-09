package de.mobile.inventorylistindexer.client;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

import de.mobile.inventorylistindexer.indexer.status.Status;


public interface OrderApi {

    @GET("status/{id}")
    Observable<Status> status(@Path("id") String adId);

}
