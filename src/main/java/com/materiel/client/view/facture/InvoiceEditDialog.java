package com.materiel.client.view.facture;

import com.materiel.client.model.Invoice;
import com.materiel.client.view.doc.DocumentLineTable;
import com.materiel.client.view.doc.DocumentTotalsPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Éditeur simple de facture.
 */
public class InvoiceEditDialog extends JDialog {
    private final Invoice invoice;
    private boolean confirmed = false;

    public InvoiceEditDialog(Frame parent, Invoice invoice){
        super(parent, "Facture", true);
        this.invoice = invoice;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        DocumentLineTable table = new DocumentLineTable(invoice.getLines());
        DocumentTotalsPanel totals = new DocumentTotalsPanel();
        totals.bind(invoice.getLines());
        table.getModel().addTableModelListener(e->{ totals.bind(invoice.getLines()); invoice.recalcTotals(); });
        add(table, BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(totals, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e->dispose());
        JButton save = new JButton("Enregistrer");
        save.addActionListener(e->{ confirmed=true; dispose(); });
        buttons.add(cancel); buttons.add(save);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
        setSize(700,400);
        setLocationRelativeTo(getParent());
    }

    public boolean isConfirmed(){ return confirmed; }
}
