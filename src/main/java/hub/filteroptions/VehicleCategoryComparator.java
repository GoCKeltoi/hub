package de.mobile.inventorylistservice.filteroptions;


import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VehicleCategoryComparator implements Comparator<VehicleCategoryOption> {
    private final Logger logger = LoggerFactory.getLogger(VehicleCategoryComparator.class);

    public int compare(VehicleCategoryOption o1, VehicleCategoryOption o2) {
        try {
            return VehicleCategory.valueOf(o1.getId()).compareTo(VehicleCategory.valueOf(o2.getId()));
        } catch (IllegalArgumentException ex) {
            logger.warn("Cannot sort VehicleCategory ${} or ${} because it does not exist in VehicleCategory enum.", o1.getId(), o2.getId());
            return o1.getTr().compareTo(o2.getTr());
        }
    }

    // enum order is defied in COMM-84
    private enum VehicleCategory {
        Car,
        Motorbike,
        Motorhome,
        VanUpTo7500,
        TruckOver7500,
        Trailer,
        SemiTrailerTruck,
        SemiTrailer,
        ConstructionMachine,
        Bus,
        AgriculturalVehicle,
        ForkliftTruck
    }

}


