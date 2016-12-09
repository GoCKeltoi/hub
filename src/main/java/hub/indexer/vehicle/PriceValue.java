package de.mobile.inventorylistindexer.indexer.vehicle;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
public class PriceValue {
    private String gross;

    private String net;

    public String getGross() {
        return this.gross;
    }

    public String getNet() {
        return this.net;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public void setNet(String net) {
        this.net = net;
    }

}
