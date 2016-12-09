package de.mobile.inventorylistindexer.indexer.vehicle;

public class VehicleEvent {
    private String vehicleId;
    private String eventType;
    private Vehicle vehicle;

    public String getVehicleId() {
        return vehicleId;
    }

    public String getEventType() {
        return eventType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
