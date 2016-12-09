package de.mobile.inventorylistindexer.indexer.vehicle;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
public class Price {
    public String currency;
    public ConsumerValue consumerValue;
    public DealerValue dealerValue;
    public String vatRate;
    public String deliveryCost;
    public String type;
    public Boolean isNet;
}
