package com.materiel.client.util;

import com.materiel.client.model.DocumentLine;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class DocumentTotalsCalculator {
    private DocumentTotalsCalculator(){}
    public static class Totaux {
        public BigDecimal totalHT, totalTVA, totalTTC;
        public Totaux(BigDecimal ht, BigDecimal tva, BigDecimal ttc){ this.totalHT=ht; this.totalTVA=tva; this.totalTTC=ttc; }
    }
    public static Totaux compute(List<DocumentLine> lines){
        BigDecimal ht = BigDecimal.ZERO, tva = BigDecimal.ZERO;
        for (DocumentLine l : lines){
            if (l==null) continue;
            BigDecimal q = nz(l.getQuantite());
            BigDecimal pu = nz(l.getPrixUnitaireHT());
            BigDecimal rem = nz(l.getRemisePct()).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            BigDecimal base = q.multiply(pu).multiply(BigDecimal.ONE.subtract(rem));
            base = base.setScale(2, RoundingMode.HALF_UP);
            BigDecimal tvaPct = nz(l.getTvaPct()).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            BigDecimal tvaL = base.multiply(tvaPct).setScale(2, RoundingMode.HALF_UP);
            ht = ht.add(base);
            tva = tva.add(tvaL);
        }
        BigDecimal ttc = ht.add(tva).setScale(2, RoundingMode.HALF_UP);
        return new Totaux(ht.setScale(2, RoundingMode.HALF_UP), tva.setScale(2, RoundingMode.HALF_UP), ttc);
    }
    private static BigDecimal nz(BigDecimal b){ return b==null? BigDecimal.ZERO : b; }
}
