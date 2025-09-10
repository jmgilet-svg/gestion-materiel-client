package com.materiel.client.mock;

import com.materiel.client.model.*;
import com.materiel.client.service.OrderService;
import java.nio.file.Path;
import com.materiel.client.service.SequenceService;

import java.time.LocalDate;
import java.util.*;

/**
 * Implémentation mock du service de commandes avec persistance JSON.
 */
public class OrderServiceMock implements OrderService {
    private final JsonStore<Order> store;
    private final SequenceService sequenceService;
    private final List<Order> cache;

    public OrderServiceMock(Path dataDir, SequenceService sequenceService) {
        this.store = new JsonStore<>(dataDir.resolve("orders.json"), Order[].class);
        this.sequenceService = sequenceService;
        this.cache = store.load();
    }

    @Override
    public List<Order> list() { return new ArrayList<>(cache); }

    @Override
    public Order get(UUID id) { return cache.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null); }

    @Override
    public Order create(Order order) {
        if (order.getId() == null) order.setId(UUID.randomUUID());
        if (order.getNumber() == null) order.setNumber(generateNumber(order.getDate()));
        order.recalcTotals();
        cache.add(order);
        store.save(cache);
        return order;
    }

    @Override
    public Order update(Order order) {
        delete(order.getId());
        order.recalcTotals();
        cache.add(order);
        store.save(cache);
        return order;
    }

    @Override
    public void delete(UUID id) {
        cache.removeIf(o -> o.getId().equals(id));
        store.save(cache);
    }

    @Override
    public Order transition(UUID id, DocumentStatus newStatus) {
        Order o = get(id);
        if (o != null) {
            o.setStatus(newStatus);
            update(o);
        }
        return o;
    }

    @Override
    public String generateNumber(LocalDate date) {
        return sequenceService.nextNumber(DocumentType.ORDER, date);
    }

    @Override
    public Order fromQuote(Quote quote) {
        Order order = new Order();
        order.setQuoteId(quote.getId());
        order.setCustomerId(quote.getCustomerId());
        order.setCustomerName(quote.getCustomerName());
        order.setLines(new ArrayList<>(quote.getLines()));
        order.setDate(LocalDate.now());
        order.recalcTotals();
        if (DocumentStatus.ACCEPTED.equals(quote.getStatus())) {
            order.setStatus(DocumentStatus.CONFIRMED);
        } else {
            order.setStatus(DocumentStatus.DRAFT);
        }
        return create(order);
    }
}
