package com.materiel.client.service.impl.backend;

import com.materiel.client.backend.api.QuotesApi;
import com.materiel.client.backend.invoker.ApiClient;
import com.materiel.client.backend.invoker.ApiException;
import com.materiel.client.backend.model.Quote;
import com.materiel.client.service.QuoteService;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation backend du service de devis.
 */
public class QuoteServiceBackend implements QuoteService {
    private final QuotesApi api;

    public QuoteServiceBackend(ApiClient client) {
        this.api = new QuotesApi(client);
    }

    @Override
    public List<Quote> list() {
        try {
            return api.listQuotes();
        } catch (ApiException e) {
            throw new RuntimeException("API error", e);
        }
    }

    @Override
    public Quote get(UUID id) {
        try {
            return api.getQuote(id);
        } catch (ApiException e) {
            throw new RuntimeException("API error", e);
        }
    }

    @Override
    public Quote create(Quote quote) {
        try {
            return api.createQuote(quote);
        } catch (ApiException e) {
            throw new RuntimeException("API error", e);
        }
    }

    @Override
    public Quote update(Quote quote) {
        try {
            return api.updateQuote(quote.getId(), quote);
        } catch (ApiException e) {
            throw new RuntimeException("API error", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            api.deleteQuote(id);
        } catch (ApiException e) {
            throw new RuntimeException("API error", e);
        }
    }
}
