package com.materiel.client.model;

import java.util.UUID;

/**
 * Commande/Order.
 */
public class Order extends BaseDocument {
    private UUID quoteId;

    public UUID getQuoteId() { return quoteId; }
    public void setQuoteId(UUID quoteId) { this.quoteId = quoteId; }
}
