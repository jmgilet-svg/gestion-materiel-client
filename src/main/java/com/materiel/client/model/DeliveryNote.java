package com.materiel.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bon de livraison / Delivery note.
 */
public class DeliveryNote extends BaseDocument {
    private UUID orderId;
    private List<UUID> interventionIds = new ArrayList<>();

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public List<UUID> getInterventionIds() { return interventionIds; }
    public void setInterventionIds(List<UUID> interventionIds) { this.interventionIds = interventionIds; }
}
