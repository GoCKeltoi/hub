package de.mobile.inventorylistindexer.client;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

import de.mobile.inventorylistindexer.indexer.images.VehicleImages;
import de.mobile.inventorylistindexer.indexer.vehicle.Vehicle;


public interface InventoryApi {

    @GET("vehicles/{id}")
    Observable<Vehicle> vehicle(@Path("id") String vehicleId);

    @GET("vehicles/{id}/images")
    Observable<VehicleImages> images(@Path("id") String vehicleId);

}
