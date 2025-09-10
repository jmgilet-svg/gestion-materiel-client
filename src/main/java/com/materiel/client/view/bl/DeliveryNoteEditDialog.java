package com.materiel.client.view.bl;

import com.materiel.client.model.DeliveryNote;
import com.materiel.client.view.doc.DocumentLineTable;
import com.materiel.client.view.doc.DocumentTotalsPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Éditeur simple de bon de livraison.
 */
public class DeliveryNoteEditDialog extends JDialog {
    private final DeliveryNote note;
    private boolean confirmed = false;

    public DeliveryNoteEditDialog(Frame parent, DeliveryNote note){
        super(parent, "Bon de livraison", true);
        this.note = note;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        DocumentLineTable table = new DocumentLineTable(note.getLines());
        DocumentTotalsPanel totals = new DocumentTotalsPanel();
        totals.bind(note.getLines());
        table.getModel().addTableModelListener(e->{ totals.bind(note.getLines()); note.recalcTotals(); });
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
