package com.materiel.client.view.commande;

import com.materiel.client.model.Order;
import com.materiel.client.view.doc.DocumentLineTable;
import com.materiel.client.view.doc.DocumentTotalsPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Éditeur simple de commande permettant de modifier les lignes.
 */
public class OrderEditDialog extends JDialog {
    private final Order order;
    private boolean confirmed = false;

    public OrderEditDialog(Frame parent, Order order){
        super(parent, "Commande", true);
        this.order = order;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        DocumentLineTable table = new DocumentLineTable(order.getLines());
        DocumentTotalsPanel totals = new DocumentTotalsPanel();
        totals.bind(order.getLines());
        table.getModel().addTableModelListener(e->{ totals.bind(order.getLines()); order.recalcTotals(); });
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
