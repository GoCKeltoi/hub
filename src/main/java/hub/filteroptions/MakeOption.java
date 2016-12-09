package de.mobile.inventorylistservice.filteroptions;

import java.util.List;
import java.util.Optional;

public class MakeOption {

    private final String id;

    private final String tr;

    private final List<ModelOption> models;

    public MakeOption(String id, Optional<String> tr, List<ModelOption> models) {
        this.id = id;
        this.tr = tr.orElse(id);
        this.models = models;
    }

    public MakeOption(String id, String tr, List<ModelOption> models) {
        this.id = id;
        this.tr = tr;
        this.models = models;
    }

    public String getId() {
        return id;
    }

    public String getTr() {
        return tr;
    }

    public List<ModelOption> getModels() {
        return models;
    }
}
