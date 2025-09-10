package com.materiel.client.mock;

import com.materiel.client.model.*;
import com.materiel.client.service.InvoiceService;
import com.materiel.client.service.SequenceService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;

/**
 * Implémentation mock des factures.
 */
public class InvoiceServiceMock implements InvoiceService {
    private final JsonStore<Invoice> store;
    private final SequenceService sequenceService;
    private final List<Invoice> cache;

    public InvoiceServiceMock(Path dataDir, SequenceService sequenceService) {
        this.store = new JsonStore<>(dataDir.resolve("invoices.json"), Invoice[].class);
        this.sequenceService = sequenceService;
        this.cache = store.load();
        if (this.cache.isEmpty()) {
            Invoice sample = new Invoice();
            sample.setCustomerId(UUID.randomUUID());
            sample.setCustomerName("Client démo");
            sample.setLines(List.of(
                new DocumentLine(UUID.randomUUID(), "Prod1", new BigDecimal("1"), "u", new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("20")),
                new DocumentLine(UUID.randomUUID(), "Prod2", new BigDecimal("1"), "u", new BigDecimal("50"), BigDecimal.ZERO, new BigDecimal("20"))
            ));
            create(sample);
        }
    }

    @Override
    public List<Invoice> list() { return new ArrayList<>(cache); }

    @Override
    public Invoice get(UUID id) { return cache.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null); }

    @Override
    public Invoice create(Invoice invoice) {
        if (invoice.getId() == null) invoice.setId(UUID.randomUUID());
        if (invoice.getNumber() == null) invoice.setNumber(generateNumber(invoice.getDate()));
        invoice.recalcTotals();
        cache.add(invoice);
        store.save(cache);
        return invoice;
    }

    @Override
    public Invoice update(Invoice invoice) {
        delete(invoice.getId());
        invoice.recalcTotals();
        cache.add(invoice);
        store.save(cache);
        return invoice;
    }

    @Override
    public void delete(UUID id) {
        cache.removeIf(o -> o.getId().equals(id));
        store.save(cache);
    }

    @Override
    public Invoice transition(UUID id, DocumentStatus newStatus) {
        Invoice inv = get(id);
        if (inv != null) {
            inv.setStatus(newStatus);
            update(inv);
        }
        return inv;
    }

    @Override
    public String generateNumber(LocalDate date) {
        return sequenceService.nextNumber(DocumentType.INVOICE, date);
    }

    @Override
    public Invoice fromQuote(Quote quote) {
        Invoice invoice = new Invoice();
        invoice.setQuoteId(quote.getId());
        invoice.setCustomerId(quote.getCustomerId());
        invoice.setCustomerName(quote.getCustomerName());
        invoice.setLines(new ArrayList<>(quote.getLines()));
        invoice.setDate(LocalDate.now());
        invoice.recalcTotals();
        invoice.setStatus(DocumentStatus.DRAFT);
        return create(invoice);
    }

    @Override
    public Invoice fromDeliveryNotes(List<DeliveryNote> notes) {
        Invoice invoice = new Invoice();
        if (!notes.isEmpty()) {
            DeliveryNote first = notes.get(0);
            invoice.setCustomerId(first.getCustomerId());
            invoice.setCustomerName(first.getCustomerName());
        }
        invoice.setDeliveryNoteIds(new ArrayList<>());
        List<DocumentLine> lines = new ArrayList<>();
        for (DeliveryNote note : notes) {
            invoice.getDeliveryNoteIds().add(note.getId());
            lines.addAll(note.getLines());
        }
        invoice.setLines(lines);
        invoice.setDate(LocalDate.now());
        invoice.recalcTotals();
        invoice.setStatus(DocumentStatus.DRAFT);
        return create(invoice);
    }
}
