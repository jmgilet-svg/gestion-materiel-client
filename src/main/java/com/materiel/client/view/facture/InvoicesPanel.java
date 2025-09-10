package com.materiel.client.view.facture;

import javax.swing.*;
import java.awt.*;

/**
 * Liste des factures (stub pour tests). Contient un label "WIRED_OK".
 */
public class InvoicesPanel extends JPanel {
    public InvoicesPanel() {
        setLayout(new BorderLayout());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("WIRED_OK"));
        add(south, BorderLayout.SOUTH);
    }
}
