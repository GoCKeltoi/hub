package de.mobile.inventorylistindexer.indexer.images;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

public class VehicleImages {

    public static final VehicleImages EMPTY = new VehicleImages(emptyList());

    private final List<Map<String, String>> images;

    public VehicleImages(List<Map<String, String>> images) {
        this.images = ImmutableList.copyOf(Objects.requireNonNull(images, "images"));
    }

    public List<Map<String, String>> getImages() {
        return images;
    }

}
