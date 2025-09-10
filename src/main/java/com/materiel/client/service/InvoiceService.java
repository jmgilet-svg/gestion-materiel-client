package com.materiel.client.service;

import com.materiel.client.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des factures.
 */
public interface InvoiceService {
    List<Invoice> list();
    Invoice get(UUID id);
    Invoice create(Invoice invoice);
    Invoice update(Invoice invoice);
    void delete(UUID id);
    Invoice transition(UUID id, DocumentStatus newStatus);
    String generateNumber(LocalDate date);

    Invoice fromQuote(Quote quote);
    Invoice fromDeliveryNotes(List<DeliveryNote> notes);
}
