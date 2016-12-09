package de.mobile.inventorylistindexer.util;

import java.util.Optional;
import java.util.Set;

public class MoreOptional {

    public static <T> Set<T> toSet(Optional<T> o) {
        return o
                .map(java.util.Collections::singleton)
                .orElse(java.util.Collections.emptySet());
    }

}
