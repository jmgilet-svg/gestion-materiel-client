package com.materiel.client.service;

import com.materiel.client.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des bons de livraison.
 */
public interface DeliveryNoteService {
    List<DeliveryNote> list();
    DeliveryNote get(UUID id);
    DeliveryNote create(DeliveryNote note);
    DeliveryNote update(DeliveryNote note);
    void delete(UUID id);
    DeliveryNote transition(UUID id, DocumentStatus newStatus);
    String generateNumber(LocalDate date);

    /**
     * Génère un bon de livraison à partir d'une commande.
     */
    DeliveryNote fromOrder(Order order, List<UUID> interventionIds);
}
