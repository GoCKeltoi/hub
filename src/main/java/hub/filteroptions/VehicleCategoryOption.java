package de.mobile.inventorylistservice.filteroptions;

import java.util.List;

public class VehicleCategoryOption {
    private final String id;

    private final String tr;

    private final List<MakeOption> makes;

    public VehicleCategoryOption(String id, String tr, List<MakeOption> makes) {
        this.id = id;
        this.tr = tr;
        this.makes = makes;
    }

    public String getId() {
        return id;
    }

    public String getTr() {
        return tr;
    }

    public List<MakeOption> getMakes() {
        return makes;
    }
}
