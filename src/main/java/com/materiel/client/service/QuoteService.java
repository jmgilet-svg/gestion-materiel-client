package com.materiel.client.service;

import com.materiel.client.backend.model.Quote;

import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des devis (quotes).
 */
public interface QuoteService {
    List<Quote> list();
    Quote get(UUID id);
    Quote create(Quote quote);
    Quote update(Quote quote);
    void delete(UUID id);
}
