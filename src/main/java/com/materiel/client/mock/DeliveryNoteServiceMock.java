package com.materiel.client.mock;

import com.materiel.client.model.*;
import com.materiel.client.service.DeliveryNoteService;
import com.materiel.client.service.SequenceService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Implémentation mock pour les bons de livraison.
 */
public class DeliveryNoteServiceMock implements DeliveryNoteService {
    private final JsonStore<DeliveryNote> store;
    private final SequenceService sequenceService;
    private final List<DeliveryNote> cache;

    public DeliveryNoteServiceMock(Path dataDir, SequenceService sequenceService) {
        this.store = new JsonStore<>(dataDir.resolve("delivery_notes.json"), DeliveryNote[].class);
        this.sequenceService = sequenceService;
        this.cache = store.load();
    }

    @Override
    public List<DeliveryNote> list() { return new ArrayList<>(cache); }

    @Override
    public DeliveryNote get(UUID id) { return cache.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null); }

    @Override
    public DeliveryNote create(DeliveryNote note) {
        if (note.getId() == null) note.setId(UUID.randomUUID());
        if (note.getNumber() == null) note.setNumber(generateNumber(note.getDate()));
        note.recalcTotals();
        cache.add(note);
        store.save(cache);
        return note;
    }

    @Override
    public DeliveryNote update(DeliveryNote note) {
        delete(note.getId());
        note.recalcTotals();
        cache.add(note);
        store.save(cache);
        return note;
    }

    @Override
    public void delete(UUID id) {
        cache.removeIf(o -> o.getId().equals(id));
        store.save(cache);
    }

    @Override
    public DeliveryNote transition(UUID id, DocumentStatus newStatus) {
        DeliveryNote bl = get(id);
        if (bl != null) {
            bl.setStatus(newStatus);
            update(bl);
        }
        return bl;
    }

    @Override
    public String generateNumber(LocalDate date) {
        return sequenceService.nextNumber(DocumentType.DELIVERY_NOTE, date);
    }

    @Override
    public DeliveryNote fromOrder(Order order, List<UUID> interventionIds) {
        DeliveryNote bl = new DeliveryNote();
        bl.setOrderId(order.getId());
        bl.setCustomerId(order.getCustomerId());
        bl.setCustomerName(order.getCustomerName());
        bl.setLines(new ArrayList<>(order.getLines()));
        bl.setInterventionIds(new ArrayList<>(interventionIds));
        bl.setDate(LocalDate.now());
        bl.recalcTotals();
        bl.setStatus(DocumentStatus.DRAFT);
        return create(bl);
    }
}
