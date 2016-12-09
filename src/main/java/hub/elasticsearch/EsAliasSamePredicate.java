package de.mobile.inventorylistindexer.elasticsearch;

import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsAliasSamePredicate implements Predicate<String> {

    private static final Logger logger = LoggerFactory.getLogger(EsAliasSamePredicate.class);

    private final EsAliasResolver aliasProvider;

    public EsAliasSamePredicate(EsAliasResolver aliasProvider) {
        this.aliasProvider = aliasProvider;
    }

    @Override
    public boolean test(String expected) {
        final Optional<String> current = aliasProvider.get();
        if (!current.isPresent()) {
            logger.warn("No alias is currently present");
            return false;
        }

        return current.get().equals(expected);
    }

}
