package com.materiel.client.view.doc;

import com.materiel.client.model.DocumentLine;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DocumentLineTable extends JPanel {
  private final JTable table;
  private final DocumentLineTableModel model;

  public DocumentLineTable(List<DocumentLine> lines){
    super(new BorderLayout());
    model = new DocumentLineTableModel(lines);
    table = new JTable(model);
    table.setRowHeight(28);
    table.putClientProperty("FlatLaf.styleClass", "table");
    table.setAutoCreateRowSorter(true);
    table.setRowSorter(new TableRowSorter<>(table.getModel()));

    // éditeurs simples : spinners pour numériques + combo TVA
    setEditors();

    JToolBar tb = new JToolBar();
    tb.setFloatable(false);
    JButton add = new JButton("Ajouter");
    JButton del = new JButton("Supprimer");
    JButton up  = new JButton("Monter");
    JButton dn  = new JButton("Descendre");
    add.addActionListener(e -> model.addEmptyLine());
    del.addActionListener(e -> model.removeAt(table.getSelectedRow()));
    up.addActionListener(e -> model.moveUp(table.getSelectedRow()));
    dn.addActionListener(e -> model.moveDown(table.getSelectedRow()));
    tb.add(add); tb.add(del); tb.addSeparator(); tb.add(up); tb.add(dn);

    add(tb, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  private void setEditors(){
    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JFormattedTextField()));
    table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JFormattedTextField()));
    table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JFormattedTextField()));
    JComboBox<BigDecimal> tva = new JComboBox<>(new BigDecimal[]{
      new BigDecimal("20.0"), new BigDecimal("10.0"), new BigDecimal("5.5"), new BigDecimal("0.0")
    });
    table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(tva));
  }

  public DocumentLineTableModel getModel(){ return model; }
}
