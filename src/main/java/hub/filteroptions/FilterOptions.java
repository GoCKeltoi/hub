package de.mobile.inventorylistservice.filteroptions;

import java.util.List;

public class FilterOptions {
    private final List<VehicleCategoryOption> vehicleCategories;

    public FilterOptions(List<VehicleCategoryOption> vehicleCategories) {
        this.vehicleCategories = vehicleCategories;
    }

    public List<VehicleCategoryOption> getVehicleCategories() {
        return vehicleCategories;
    }
}
