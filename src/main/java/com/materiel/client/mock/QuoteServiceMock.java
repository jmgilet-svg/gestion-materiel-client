package com.materiel.client.mock;

import com.materiel.client.backend.model.Quote;
import com.materiel.client.service.QuoteService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation mock du service de devis.
 */
public class QuoteServiceMock implements QuoteService {
    private final JsonStore<Quote> store;
    private final List<Quote> cache;

    public QuoteServiceMock(Path dataDir) {
        this.store = new JsonStore<>(dataDir.resolve("quotes.json"), Quote[].class);
        this.cache = store.load();
    }

    @Override
    public List<Quote> list() {
        return new ArrayList<>(cache);
    }

    @Override
    public Quote get(UUID id) {
        return cache.stream().filter(q -> id.equals(q.getId())).findFirst().orElse(null);
    }

    @Override
    public Quote create(Quote quote) {
        if (quote.getId() == null) {
            quote.setId(UUID.randomUUID());
        }
        cache.add(quote);
        store.save(cache);
        return quote;
    }

    @Override
    public Quote update(Quote quote) {
        delete(quote.getId());
        cache.add(quote);
        store.save(cache);
        return quote;
    }

    @Override
    public void delete(UUID id) {
        cache.removeIf(q -> id.equals(q.getId()));
        store.save(cache);
    }
}
