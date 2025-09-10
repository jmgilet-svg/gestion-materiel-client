package com.materiel.client.view.commande;

import javax.swing.*;
import java.awt.*;

/**
 * Liste des commandes (stub pour tests). Contient un label "WIRED_OK".
 */
public class OrdersPanel extends JPanel {
    public OrdersPanel() {
        setLayout(new BorderLayout());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("WIRED_OK"));
        add(south, BorderLayout.SOUTH);
    }
}
