package com.materiel.client.service;

import com.materiel.client.mock.DeliveryNoteServiceMock;
import com.materiel.client.mock.InvoiceServiceMock;
import com.materiel.client.mock.OrderServiceMock;
import com.materiel.client.mock.SequenceServiceMock;
import com.materiel.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceMockTest {

    private Path dataDir;
    private SequenceServiceMock sequenceService;
    private OrderServiceMock orderService;
    private DeliveryNoteServiceMock deliveryNoteService;
    private InvoiceServiceMock invoiceService;

    @BeforeEach
    void setup() throws Exception {
        dataDir = Files.createTempDirectory("gm-data");
        sequenceService = new SequenceServiceMock(dataDir);
        orderService = new OrderServiceMock(dataDir, sequenceService);
        deliveryNoteService = new DeliveryNoteServiceMock(dataDir, sequenceService);
        invoiceService = new InvoiceServiceMock(dataDir, sequenceService);
    }

    @Test
    void fromQuoteToOrder() {
        Quote quote = new Quote();
        quote.setId(UUID.randomUUID());
        quote.setCustomerId(UUID.randomUUID());
        quote.setCustomerName("Client");
        quote.setStatus(DocumentStatus.ACCEPTED);
        quote.setLines(List.of(
                new DocumentLine(UUID.randomUUID(), "Prod1", BigDecimal.valueOf(2), "u", BigDecimal.valueOf(100), BigDecimal.valueOf(10), BigDecimal.valueOf(20)),
                new DocumentLine(UUID.randomUUID(), "Prod2", BigDecimal.ONE, "u", BigDecimal.valueOf(50), BigDecimal.ZERO, BigDecimal.valueOf(20))
        ));
        quote.recalcTotals();

        Order order = orderService.fromQuote(quote);
        assertThat(order.getQuoteId()).isEqualTo(quote.getId());
        assertThat(order.getStatus()).isEqualTo(DocumentStatus.CONFIRMED);
        assertThat(order.getNumber()).isNotNull();
        assertThat(order.getLines()).hasSize(2);
        for (int i = 0; i < 2; i++) {
            DocumentLine ql = quote.getLines().get(i);
            DocumentLine ol = order.getLines().get(i);
            assertThat(ol.getDesignation()).isEqualTo(ql.getDesignation());
            assertThat(ol.getPrixUnitaireHT()).isEqualByComparingTo(ql.getPrixUnitaireHT());
            assertThat(ol.getTvaPct()).isEqualByComparingTo(ql.getTvaPct());
        }
        assertThat(order.getTotalHT()).isEqualByComparingTo(BigDecimal.valueOf(230));
        assertThat(order.getTotalTVA()).isEqualByComparingTo(BigDecimal.valueOf(46));
        assertThat(order.getTotalTTC()).isEqualByComparingTo(BigDecimal.valueOf(276));
    }

    @Test
    void fromOrderToDeliveryNote() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        order.setCustomerName("Client");
        order.setLines(List.of(
                new DocumentLine(UUID.randomUUID(), "Prod1", BigDecimal.ONE, "u", BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.valueOf(20))
        ));
        order.recalcTotals();
        orderService.create(order);

        DeliveryNote bl = deliveryNoteService.fromOrder(order, List.of(UUID.randomUUID()));
        assertThat(bl.getOrderId()).isEqualTo(order.getId());
        assertThat(bl.getInterventionIds()).hasSize(1);
        assertThat(bl.getTotalHT()).isEqualByComparingTo(order.getTotalHT());
    }

    @Test
    void fromBLToInvoice() {
        DeliveryNote bl1 = new DeliveryNote();
        bl1.setId(UUID.randomUUID());
        bl1.setCustomerId(UUID.randomUUID());
        bl1.setCustomerName("Client");
        bl1.setLines(List.of(
                new DocumentLine(UUID.randomUUID(), "Prod1", BigDecimal.ONE, "u", BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.valueOf(20))
        ));
        bl1.recalcTotals();
        deliveryNoteService.create(bl1);

        DeliveryNote bl2 = new DeliveryNote();
        bl2.setId(UUID.randomUUID());
        bl2.setCustomerId(bl1.getCustomerId());
        bl2.setCustomerName("Client");
        bl2.setLines(List.of(
                new DocumentLine(UUID.randomUUID(), "Prod2", BigDecimal.ONE, "u", BigDecimal.valueOf(50), BigDecimal.ZERO, BigDecimal.valueOf(20))
        ));
        bl2.recalcTotals();
        deliveryNoteService.create(bl2);

        Invoice invoice = invoiceService.fromDeliveryNotes(List.of(bl1, bl2));
        assertThat(invoice.getDeliveryNoteIds()).containsExactlyInAnyOrder(bl1.getId(), bl2.getId());
        assertThat(invoice.getTotalTVA()).isEqualByComparingTo(bl1.getTotalTVA().add(bl2.getTotalTVA()));
    }
}
