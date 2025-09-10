package com.materiel.client.view.doc;

import com.materiel.client.model.DocumentLine;
import com.materiel.client.util.DocumentTotalsCalculator;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DocumentTotalsPanel extends JPanel {
  private final JLabel ht = new JLabel("0.00 €");
  private final JLabel tva = new JLabel("0.00 €");
  private final JLabel ttc = new JLabel("0.00 €");

  public DocumentTotalsPanel(){
    super(new GridLayout(3,2,8,4));
    add(new JLabel("Total HT :")); add(ht);
    add(new JLabel("Total TVA :")); add(tva);
    add(new JLabel("Total TTC :")); add(ttc);
  }
  public void bind(List<DocumentLine> lines){
    var t = DocumentTotalsCalculator.compute(lines);
    ht.setText(fmt(t.totalHT)); tva.setText(fmt(t.totalTVA)); ttc.setText(fmt(t.totalTTC));
  }
  private static String fmt(BigDecimal v){ return String.format("%,.2f €", v); }
}
