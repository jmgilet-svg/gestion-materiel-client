// InterventionCard.java
package com.materiel.client.view.components;

import com.materiel.client.model.Intervention;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

/**
 * Carte représentant une intervention dans le planning
 */
public class InterventionCard extends JPanel {
    
    private final Intervention intervention;
    private boolean hovered = false;
    private boolean selected = false;
    
    public InterventionCard(Intervention intervention) {
        this.intervention = intervention;
        initComponents();
        setupEventHandlers();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 3));
        setPreferredSize(new Dimension(140, 80));
        setOpaque(true);
        updateAppearance();
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Header avec le statut
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenu principal
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer avec les heures
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Pastille de statut
        JLabel statusLabel = new JLabel("●");
        statusLabel.setForeground(Color.decode(intervention.getStatut().getColor()));
        statusLabel.setFont(statusLabel.getFont().deriveFont(12f));
        statusLabel.setToolTipText(intervention.getStatut().getDisplayName());
        
        // Titre tronqué
        JLabel titleLabel = new JLabel(truncateText(intervention.getTitre(), 15));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 11f));
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Client
        if (intervention.getClient() != null) {
            JLabel clientLabel = new JLabel("👤 " + truncateText(intervention.getClient().getNom(), 12));
            clientLabel.setFont(clientLabel.getFont().deriveFont(10f));
            clientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(clientLabel);
        }
        
        // Adresse d'intervention
        if (intervention.getAdresseIntervention() != null) {
            JLabel adresseLabel = new JLabel("📍 " + truncateText(intervention.getAdresseIntervention(), 12));
            adresseLabel.setFont(adresseLabel.getFont().deriveFont(10f));
            adresseLabel.setForeground(Color.GRAY);
            adresseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(adresseLabel);
        }
        
        // Notes (si présentes)
        if (intervention.getNotes() != null && !intervention.getNotes().trim().isEmpty()) {
            JLabel notesLabel = new JLabel("📝 " + truncateText(intervention.getNotes(), 10));
            notesLabel.setFont(notesLabel.getFont().deriveFont(9f));
            notesLabel.setForeground(Color.GRAY);
            notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(notesLabel);
        }
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String heureDebut = intervention.getDateDebut() != null ? 
                           intervention.getDateDebut().format(timeFormatter) : "?";
        String heureFin = intervention.getDateFin() != null ? 
                         intervention.getDateFin().format(timeFormatter) : "?";
        
        JLabel timeLabel = new JLabel("🕐 " + heureDebut + " - " + heureFin);
        timeLabel.setFont(timeLabel.getFont().deriveFont(9f));
        timeLabel.setForeground(Color.GRAY);
        
        panel.add(timeLabel);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                updateAppearance();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                updateAppearance();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selected = !selected;
                    updateAppearance();
                } else if (e.getClickCount() == 2) {
                    showInterventionDetails();
                }
                
                // Menu contextuel sur clic droit
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getX(), e.getY());
                }
            }
        });
        
        // Support des raccourcis clavier
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }
    
    private void updateAppearance() {
        Color backgroundColor;
        Color borderColor;
        int borderWidth;
        
        if (selected) {
            backgroundColor = Color.decode("#EBF4FF");
            borderColor = Color.decode("#3B82F6");
            borderWidth = 2;
        } else if (hovered) {
            backgroundColor = Color.decode("#F8FAFC");
            borderColor = Color.decode(intervention.getStatut().getColor());
            borderWidth = 2;
        } else {
            backgroundColor = Color.WHITE;
            borderColor = Color.decode(intervention.getStatut().getColor());
            borderWidth = 1;
        }
        
        setBackground(backgroundColor);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, borderWidth, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        repaint();
    }
    
    private void showInterventionDetails() {
        String details = formatInterventionDetails();
        JOptionPane.showMessageDialog(this, details, "Détails de l'intervention", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String formatInterventionDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intervention: ").append(intervention.getTitre()).append("\n");
        sb.append("Statut: ").append(intervention.getStatut().getDisplayName()).append("\n");
        
        if (intervention.getClient() != null) {
            sb.append("Client: ").append(intervention.getClient().getNom()).append("\n");
        }
        
        if (intervention.getDateDebut() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            sb.append("Début: ").append(intervention.getDateDebut().format(formatter)).append("\n");
        }
        
        if (intervention.getDateFin() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            sb.append("Fin: ").append(intervention.getDateFin().format(formatter)).append("\n");
        }
        
        if (intervention.getAdresseIntervention() != null) {
            sb.append("Adresse: ").append(intervention.getAdresseIntervention()).append("\n");
        }
        
        if (intervention.getDescription() != null) {
            sb.append("Description: ").append(intervention.getDescription()).append("\n");
        }
        
        if (intervention.getNotes() != null) {
            sb.append("Notes: ").append(intervention.getNotes()).append("\n");
        }
        
        return sb.toString();
    }
    
    private void showContextMenu(int x, int y) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        // Modifier
        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setIcon(new javax.swing.ImageIcon("🖊️".getBytes()));
        editItem.addActionListener(e -> editIntervention());
        contextMenu.add(editItem);
        
        // Dupliquer (Ctrl+D)
        JMenuItem duplicateItem = new JMenuItem("Dupliquer");
        duplicateItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        duplicateItem.addActionListener(e -> duplicateIntervention());
        contextMenu.add(duplicateItem);
        
        contextMenu.addSeparator();
        
        // Transformer en devis
        JMenuItem transformItem = new JMenuItem("Transformer en devis");
        transformItem.addActionListener(e -> transformToDevis());
        contextMenu.add(transformItem);
        
        contextMenu.addSeparator();
        
        // Supprimer (Suppr)
        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        deleteItem.addActionListener(e -> deleteIntervention());
        contextMenu.add(deleteItem);
        
        contextMenu.show(this, x, y);
    }
    
    private void handleKeyPress(java.awt.event.KeyEvent e) {
        switch (e.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_DELETE:
                deleteIntervention();
                break;
            case java.awt.event.KeyEvent.VK_D:
                if (e.isControlDown()) {
                    duplicateIntervention();
                }
                break;
            case java.awt.event.KeyEvent.VK_ENTER:
                showInterventionDetails();
                break;
        }
    }
    
    private void editIntervention() {
        // TODO: Ouvrir le dialogue d'édition
        JOptionPane.showMessageDialog(this, "Édition de l'intervention à implémenter");
    }
    
    private void duplicateIntervention() {
        // TODO: Dupliquer l'intervention
        JOptionPane.showMessageDialog(this, "Duplication de l'intervention: " + intervention.getTitre());
    }
    
    private void transformToDevis() {
        // TODO: Transformer en devis
        JOptionPane.showMessageDialog(this, "Transformation en devis pour: " + intervention.getTitre());
    }
    
    private void deleteIntervention() {
        int result = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer cette intervention ?\n" + intervention.getTitre(),
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            // TODO: Supprimer l'intervention
            JOptionPane.showMessageDialog(this, "Intervention supprimée: " + intervention.getTitre());
        }
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    public Intervention getIntervention() {
        return intervention;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateAppearance();
    }
} 
