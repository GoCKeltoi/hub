package de.mobile.inventorylistservice.filteroptions;

import dagger.Module;
import dagger.Provides;
import de.mobile.inventorylistservice.searchgateway.SearchGateway;
import de.mobile.inventorylistservice.translation.Translator;
import de.mobile.inventorylistservice.translation.makemodel.MakeModelResolver;

@Module
public class FilterOptionsModule {


    @Provides
    FilterOptionsService filterOptionsService(SearchGateway gateway, MakeModelResolver resolver, Translator translator) {
        return new FilterOptionsServiceImpl(gateway, resolver, translator);
    }
}
