package com.materiel.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Facture / Invoice.
 */
public class Invoice extends BaseDocument {
    private UUID quoteId;
    private List<UUID> deliveryNoteIds = new ArrayList<>();

    public UUID getQuoteId() { return quoteId; }
    public void setQuoteId(UUID quoteId) { this.quoteId = quoteId; }
    public List<UUID> getDeliveryNoteIds() { return deliveryNoteIds; }
    public void setDeliveryNoteIds(List<UUID> deliveryNoteIds) { this.deliveryNoteIds = deliveryNoteIds; }
}
