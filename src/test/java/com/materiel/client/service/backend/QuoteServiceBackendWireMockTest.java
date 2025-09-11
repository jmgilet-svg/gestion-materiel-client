package com.materiel.client.service.backend;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.materiel.client.backend.model.DocumentLine;
import com.materiel.client.backend.model.Quote;
import com.materiel.client.config.AppConfig;
import com.materiel.client.service.QuoteService;
import com.materiel.client.service.ServiceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests du QuoteServiceBackend avec WireMock.
 */
public class QuoteServiceBackendWireMockTest {
    private WireMockServer server;

    @BeforeEach
    void setup() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        WireMock.configureFor("localhost", server.port());
        System.setProperty("app.mode", "backend");
        System.setProperty("api.baseUrl", server.baseUrl());
        AppConfig.reset();
        ServiceFactory.resetServices();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        System.clearProperty("app.mode");
        System.clearProperty("api.baseUrl");
        AppConfig.reset();
        ServiceFactory.resetServices();
    }

    @Test
    void testListAndCreateQuote() {
        UUID quoteId = UUID.randomUUID();
        UUID lineId = UUID.randomUUID();
        String listBody = "[{'id':'"+quoteId+"','number':'Q-1','date':'2024-01-10','customerName':'Bob','lines':[]," +
                "'totalHT':0,'totalTVA':0,'totalTTC':0,'status':'DRAFT'}]".replace(''','"');
        server.stubFor(get(urlEqualTo("/api/v1/quotes"))
                .willReturn(okJson(listBody)));

        String postResp = ("{'id':'"+quoteId+"','number':'Q-1','date':'2024-01-10','customerName':'Bob',"+
                "'lines':[{'id':'"+lineId+"','designation':'Item','unite':'u','quantite':1,"+
                "'prixUnitaireHT':10,'remisePct':0,'tvaPct':20}],"+
                "'totalHT':10,'totalTVA':2,'totalTTC':12,'status':'DRAFT'}").replace(''','"');
        server.stubFor(post(urlEqualTo("/api/v1/quotes"))
                .willReturn(aResponse().withStatus(201).withHeader("Content-Type","application/json").withBody(postResp)));
        server.stubFor(put(urlEqualTo("/api/v1/quotes/"+quoteId))
                .willReturn(okJson(postResp)));

        QuoteService service = ServiceFactory.getQuoteService();
        List<Quote> quotes = service.list();
        assertEquals(1, quotes.size());
        assertEquals(LocalDate.of(2024,1,10), quotes.get(0).getDate());

        Quote q = new Quote();
        q.setNumber("Q-1");
        q.setDate(LocalDate.of(2024,1,10));
        q.setCustomerName("Bob");
        DocumentLine dl = new DocumentLine();
        dl.setId(lineId);
        dl.setDesignation("Item");
        dl.setUnite("u");
        dl.setQuantite(BigDecimal.ONE);
        dl.setPrixUnitaireHT(new BigDecimal("10"));
        dl.setRemisePct(BigDecimal.ZERO);
        dl.setTvaPct(new BigDecimal("20"));
        q.setLines(List.of(dl));
        q.setTotalHT(new BigDecimal("10"));
        q.setTotalTVA(new BigDecimal("2"));
        q.setTotalTTC(new BigDecimal("12"));

        Quote created = service.create(q);
        assertEquals(new BigDecimal("12"), created.getTotalTTC());
        service.update(created);

        verify(postRequestedFor(urlEqualTo("/api/v1/quotes"))
                .withRequestBody(matchingJsonPath("$.date", equalTo("2024-01-10")))
                .withRequestBody(matchingJsonPath("$.lines[0].designation", equalTo("Item")))
                .withRequestBody(matchingJsonPath("$.totalHT", equalTo(10.0))));
    }
}
