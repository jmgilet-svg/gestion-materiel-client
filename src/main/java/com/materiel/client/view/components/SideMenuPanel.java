// SideMenuPanel.java
package com.materiel.client.view.components;

import com.materiel.client.controller.EventBus;
import com.materiel.client.controller.events.MenuSelectionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu latéral de navigation
 */
public class SideMenuPanel extends JPanel {
    
    private final List<MenuButton> menuButtons = new ArrayList<>();
    private MenuButton selectedButton;
    
    public SideMenuPanel() {
        initComponents();
        setupPanel();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Header du menu
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Boutons de navigation
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.CENTER);
        
        // Footer avec info version
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel logoLabel = new JLabel("🏗️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel("<html><b>Gestion Matériel</b></html>");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(logoLabel);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Créer les boutons de menu
        addMenuButton(panel, "📅", "Planning", "PLANNING", true);
        addSeparator(panel);
        addMenuButton(panel, "📋", "Devis", "DEVIS", false);
        addMenuButton(panel, "📦", "Commandes", "COMMANDES", false);
        addMenuButton(panel, "🚚", "Bons de livraison", "BONS_LIVRAISON", false);
        addMenuButton(panel, "🧾", "Factures", "FACTURES", false);
        addSeparator(panel);
        addMenuButton(panel, "👥", "Clients", "CLIENTS", false);
        addMenuButton(panel, "🏗️", "Ressources", "RESSOURCES", false);
        
        return panel;
    }
    
    private void addMenuButton(JPanel parent, String icon, String text, String action, boolean selected) {
        MenuButton button = new MenuButton(icon, text, action);
        
        if (selected) {
            button.setSelected(true);
            selectedButton = button;
        }
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectButton(button);
                EventBus.getInstance().publish(new MenuSelectionEvent(action));
            }
        });
        
        menuButtons.add(button);
        parent.add(button);
        parent.add(Box.createVerticalStrut(2));
    }
    
    private void addSeparator(JPanel parent) {
        parent.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        parent.add(separator);
        parent.add(Box.createVerticalStrut(10));
    }
    
    private void selectButton(MenuButton button) {
        if (selectedButton != null) {
            selectedButton.setSelected(false);
        }
        button.setSelected(true);
        selectedButton = button;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel versionLabel = new JLabel("<html><small>Version 1.0.0</small></html>");
        versionLabel.setForeground(Color.GRAY);
        panel.add(versionLabel);
        
        return panel;
    }
    
    private void setupPanel() {
        // Configuration additionnelle si nécessaire
    }
    
    /**
     * Bouton de menu personnalisé
     */
    private static class MenuButton extends JPanel {
        private final String icon;
        private final String text;
        private final String action;
        private boolean selected = false;
        private boolean hovered = false;
        
        public MenuButton(String icon, String text, String action) {
            this.icon = icon;
            this.text = text;
            this.action = action;
            
            setOpaque(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setPreferredSize(new Dimension(0, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Arrière-plan
            if (selected) {
                g2.setColor(Color.decode("#EBF4FF"));
                g2.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 8, 8);
            } else if (hovered) {
                g2.setColor(Color.decode("#F8FAFC"));
                g2.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 8, 8);
            }
            
            // Icône
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            FontMetrics iconMetrics = g2.getFontMetrics();
            int iconY = (getHeight() + iconMetrics.getAscent()) / 2 - 2;
            g2.drawString(icon, 20, iconY);
            
            // Texte
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(selected ? Color.decode("#3B82F6") : Color.decode("#1E293B"));
            FontMetrics textMetrics = g2.getFontMetrics();
            int textY = (getHeight() + textMetrics.getAscent()) / 2 - 2;
            g2.drawString(text, 45, textY);
            
            g2.dispose();
        }
    }
} 
