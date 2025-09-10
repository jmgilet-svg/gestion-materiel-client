package com.materiel.client.view.devis;

import com.materiel.client.model.Devis;
import com.materiel.client.view.doc.DocumentLineTableModel;
import com.materiel.client.view.doc.DocumentTotalsPanel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie que la modification des lignes met bien à jour les totaux.
 */
public class DevisLineTotalsTest {
    @Test
    void editingLineUpdatesTotals(){
        System.setProperty("java.awt.headless", "true");
        Devis devis = new Devis();
        DocumentLineTableModel model = new DocumentLineTableModel(devis.getLignes());
        DocumentTotalsPanel totals = new DocumentTotalsPanel();
        totals.bind(devis.getLignes());
        model.addTableModelListener(e->{ totals.bind(devis.getLignes()); devis.recalculerMontants(); });
        model.addEmptyLine();
        model.setValueAt(new BigDecimal("100"),0,3); // PU HT
        model.setValueAt(new BigDecimal("20"),0,5);  // TVA %
        assertEquals(new BigDecimal("100.00"), devis.getMontantHT());
        assertEquals(new BigDecimal("20.00"), devis.getMontantTVA());
        assertEquals(new BigDecimal("120.00"), devis.getMontantTTC());
    }
}
