package com.materiel.client.util;

import com.materiel.client.model.DocumentLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTotalsCalculatorTest {
    @Test
    void computeTotals() {
        DocumentLine l1 = new DocumentLine();
        l1.setDesignation("L1");
        l1.setQuantite(new BigDecimal("2"));
        l1.setPrixUnitaireHT(new BigDecimal("100"));
        l1.setRemisePct(new BigDecimal("10"));
        l1.setTvaPct(new BigDecimal("20"));

        DocumentLine l2 = new DocumentLine();
        l2.setDesignation("L2");
        l2.setQuantite(new BigDecimal("1"));
        l2.setPrixUnitaireHT(new BigDecimal("50"));
        l2.setRemisePct(BigDecimal.ZERO);
        l2.setTvaPct(new BigDecimal("5.5"));

        var tot = DocumentTotalsCalculator.compute(Arrays.asList(l1, l2));

        assertEquals(new BigDecimal("230.00"), tot.totalHT);
        assertEquals(new BigDecimal("38.75"), tot.totalTVA);
        assertEquals(new BigDecimal("268.75"), tot.totalTTC);
    }
}
