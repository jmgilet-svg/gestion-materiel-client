package com.materiel.client.view.bl;

import javax.swing.*;
import java.awt.*;

/**
 * Liste des bons de livraison (stub pour tests). Contient un label "WIRED_OK".
 */
public class DeliveryNotesPanel extends JPanel {
    public DeliveryNotesPanel() {
        setLayout(new BorderLayout());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("WIRED_OK"));
        add(south, BorderLayout.SOUTH);
    }
}
