package de.mobile.inventorylistindexer.indexer.status;

public class Status {
    private Value reserved;

    private Value uploadSticky;

    public Value getReserved() {
        return this.reserved;
    }

    public Value getUploadSticky() {
        return this.uploadSticky;
    }

    public String getRenewalDate() {
        return null;
    }

    public void setReserved(Value reserved) {
        this.reserved = reserved;
    }

    public void setUploadSticky(Value uploadSticky) {
        this.uploadSticky = uploadSticky;
    }

}
