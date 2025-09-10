package com.materiel.client.model;

import java.util.Set;

/**
 * Statut générique des documents.
 * Chaque statut connaît les types auxquels il peut s'appliquer.
 */
public enum DocumentStatus {
    DRAFT(DocumentType.QUOTE, DocumentType.ORDER, DocumentType.DELIVERY_NOTE, DocumentType.INVOICE),
    SENT(DocumentType.QUOTE, DocumentType.INVOICE),
    ACCEPTED(DocumentType.QUOTE),
    REFUSED(DocumentType.QUOTE),
    EXPIRED(DocumentType.QUOTE),
    CONFIRMED(DocumentType.ORDER),
    CANCELED(DocumentType.ORDER, DocumentType.INVOICE),
    SIGNED(DocumentType.DELIVERY_NOTE),
    LOCKED(DocumentType.DELIVERY_NOTE),
    PARTIALLY_PAID(DocumentType.INVOICE),
    PAID(DocumentType.INVOICE);

    private final Set<DocumentType> types;

    DocumentStatus(DocumentType... types) {
        this.types = Set.of(types);
    }

    /**
     * Indique si le statut est applicable au type de document fourni.
     */
    public boolean isAllowed(DocumentType type) {
        return types.contains(type);
    }
}
