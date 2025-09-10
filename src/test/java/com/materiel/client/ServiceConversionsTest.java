package com.materiel.client;

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

/**
 * Teste les conversions de documents Quote -> Order -> BL -> Invoice.
 */
public class ServiceConversionsTest {
    @Test
    void flowShouldCopyLinesAndTotals() throws Exception {
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
        line.setQuantite(new BigDecimal("2"));
        line.setPrixUnitaireHT(new BigDecimal("5"));
        quote.setLines(List.of(line));
        quote.recalcTotals();

        Order order = orderService.fromQuote(quote);
        assertEquals(1, order.getLines().size());
        assertEquals(quote.getTotalTTC(), order.getTotalTTC());

        DeliveryNote bl = blService.fromOrder(order, List.of());
        assertEquals(1, bl.getLines().size());
        assertEquals(order.getTotalTTC(), bl.getTotalTTC());

        Invoice invoice = invoiceService.fromDeliveryNotes(List.of(bl));
        assertEquals(1, invoice.getLines().size());
        assertEquals(bl.getTotalTTC(), invoice.getTotalTTC());
    }
}
