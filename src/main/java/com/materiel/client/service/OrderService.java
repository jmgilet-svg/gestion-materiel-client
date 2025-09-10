package com.materiel.client.service;

import com.materiel.client.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des commandes.
 */
public interface OrderService {
    List<Order> list();
    Order get(UUID id);
    Order create(Order order);
    Order update(Order order);
    void delete(UUID id);
    Order transition(UUID id, DocumentStatus newStatus);
    String generateNumber(LocalDate date);

    /**
     * Crée une commande à partir d'un devis.
     */
    Order fromQuote(Quote quote);
}
