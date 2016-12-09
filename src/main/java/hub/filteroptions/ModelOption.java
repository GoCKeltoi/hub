package de.mobile.inventorylistservice.filteroptions;

import java.util.Optional;

public class ModelOption {
    private final String id;

    private final String tr;

    public ModelOption(String id, Optional<String> tr) {
        this.id = id;
        this.tr = tr.orElse(id);
    }

    public ModelOption(String id, String tr) {
        this.id = id;
        this.tr = tr;
    }

    public String getId() {
        return id;
    }

    public String getTr() {
        return tr;
    }
}
