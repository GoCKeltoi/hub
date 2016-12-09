package de.mobile.inventorylistservice.filteroptions;

import de.mobile.inventorylistservice.searchgateway.SearchRequest;

import java.util.Locale;

public interface FilterOptionsService {
    FilterOptions options(SearchRequest searchRequest, Locale locale);
}
