package com.materiel.client.service;

import com.materiel.client.mock.DeliveryNoteServiceMock;
import com.materiel.client.mock.InvoiceServiceMock;
import com.materiel.client.mock.OrderServiceMock;
import com.materiel.client.mock.SequenceServiceMock;
import com.materiel.client.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversionsTest {
    @Test
    void testConversionsChain() throws Exception {
        Path temp = Files.createTempDirectory("data");
        SequenceServiceMock seq = new SequenceServiceMock(temp);
        OrderServiceMock orderService = new OrderServiceMock(temp, seq);
        DeliveryNoteServiceMock blService = new DeliveryNoteServiceMock(temp, seq);
        InvoiceServiceMock invoiceService = new InvoiceServiceMock(temp, seq);

        Quote quote = new Quote();
        quote.setId(UUID.randomUUID());
        quote.setCustomerId(UUID.randomUUID());
        quote.setCustomerName("Client");
        DocumentLine line = new DocumentLine();
        line.setDesignation("Prod");
        line.setUnite("u");
        line.setQuantite(new BigDecimal("3"));
        line.setPrixUnitaireHT(new BigDecimal("10"));
        line.setRemisePct(BigDecimal.ZERO);
        line.setTvaPct(new BigDecimal("20"));
        quote.setLines(List.of(line));
        quote.recalcTotals();

        Order order = orderService.fromQuote(quote);
        assertEquals(1, order.getLines().size());
        assertEquals("Prod", order.getLines().get(0).getDesignation());
        assertEquals(quote.getTotalTTC(), order.getTotalTTC());

        DeliveryNote bl = blService.fromOrder(order, List.of());
        assertEquals(1, bl.getLines().size());
        assertEquals("Prod", bl.getLines().get(0).getDesignation());
        assertEquals(order.getTotalTTC(), bl.getTotalTTC());

        Invoice invoice = invoiceService.fromDeliveryNotes(List.of(bl));
        assertEquals(1, invoice.getLines().size());
        assertEquals("Prod", invoice.getLines().get(0).getDesignation());
        assertEquals(bl.getTotalTTC(), invoice.getTotalTTC());

        Invoice inv2 = invoiceService.fromQuote(quote);
        assertEquals(1, inv2.getLines().size());
        assertEquals("Prod", inv2.getLines().get(0).getDesignation());
        assertEquals(quote.getTotalTTC(), inv2.getTotalTTC());
    }
}
