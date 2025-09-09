// ResourceCard.java
package com.materiel.client.view.components;

import com.materiel.client.model.Resource;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.InterventionService;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carte représentant une ressource avec support drag & drop et détection de conflits
 */
public class ResourceCard extends JPanel implements DragGestureListener, DragSourceListener {
    
    private final Resource resource;
    private boolean hovered = false;
    private boolean dragging = false;
    private boolean hasConflict = false;
    private boolean isOccupied = false;
    private DragSource dragSource;
    
    // Couleurs pour les différents états
    private static final Color COLOR_AVAILABLE = Color.decode("#10B981");      // Vert - Disponible
    private static final Color COLOR_OCCUPIED = Color.decode("#F59E0B");       // Orange - Occupé
    private static final Color COLOR_CONFLICT = Color.decode("#EF4444");       // Rouge - Conflit
    private static final Color COLOR_UNAVAILABLE = Color.decode("#6B7280");    // Gris - Indisponible
    private static final Color COLOR_BORDER_NORMAL = Color.decode("#E5E7EB");  // Bordure normale
    private static final Color COLOR_BORDER_HOVER = Color.decode("#3B82F6");   // Bordure hover
    private static final Color COLOR_BACKGROUND_HOVER = Color.decode("#F8FAFC");
    
    public ResourceCard(Resource resource) {
        this.resource = resource;
        initComponents();
        setupDragAndDrop();
        checkConflictStatus();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 5));
        setPreferredSize(new Dimension(220, 70));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        setOpaque(true);
        setBackground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Icône du type de ressource avec statut
        JPanel iconPanel = createIconPanel();
        add(iconPanel, BorderLayout.WEST);
        
        // Informations de la ressource
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        
        // Indicateur de statut détaillé
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.EAST);
        
        updateAppearance();
        
        // Gestion des événements de survol
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!dragging) {
                    hovered = true;
                    updateAppearance();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                updateAppearance();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showResourceDetails();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getX(), e.getY());
                }
            }
        });
    }
    
    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(50, 60));
        
        // Icône principale
        JLabel iconLabel = new JLabel(getResourceIcon(), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        // Badge de statut (petit indicateur coloré)
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        badgePanel.setOpaque(false);
        
        JLabel statusBadge = new JLabel("●");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(getStatusColor());
        
        badgePanel.add(statusBadge);
        
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(badgePanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Nom de la ressource
        JLabel nameLabel = new JLabel(resource.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.decode("#1F2937"));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Type de ressource
        JLabel typeLabel = new JLabel(resource.getType().getDisplayName());
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeLabel.setForeground(Color.decode("#6B7280"));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Statut textuel avec couleur
        JLabel statusLabel = new JLabel(getStatusText());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(getStatusColor());
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(40, 60));
        
        // Indicateur principal de statut
        JLabel mainStatusLabel = new JLabel(getStatusIcon());
        mainStatusLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        mainStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainStatusLabel.setToolTipText(getStatusTooltip());
        
        // Indicateur de conflit si applicable
        if (hasConflict) {
            JLabel conflictLabel = new JLabel("⚠️");
            conflictLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            conflictLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            conflictLabel.setToolTipText("Conflit détecté !");
            panel.add(conflictLabel);
        }
        
        panel.add(Box.createVerticalGlue());
        panel.add(mainStatusLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void updateAppearance() {
        Color borderColor;
        Color backgroundColor = Color.WHITE;
        int borderWidth = 2;
        
        if (hasConflict) {
            borderColor = COLOR_CONFLICT;
            backgroundColor = Color.decode("#FEF2F2"); // Rouge très clair
        } else if (hovered && !dragging) {
            borderColor = COLOR_BORDER_HOVER;
            backgroundColor = COLOR_BACKGROUND_HOVER;
            borderWidth = 3;
        } else if (!resource.isDisponible()) {
            borderColor = COLOR_UNAVAILABLE;
            backgroundColor = Color.decode("#F9FAFB"); // Gris très clair
        } else if (isOccupied) {
            borderColor = COLOR_OCCUPIED;
            backgroundColor = Color.decode("#FFFBEB"); // Orange très clair
        } else {
            borderColor = COLOR_AVAILABLE;
            backgroundColor = Color.decode("#F0FDF4"); // Vert très clair
        }
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, borderWidth, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setBackground(backgroundColor);
        
        repaint();
    }
    
    private Color getStatusColor() {
        if (hasConflict) return COLOR_CONFLICT;
        if (!resource.isDisponible()) return COLOR_UNAVAILABLE;
        if (isOccupied) return COLOR_OCCUPIED;
        return COLOR_AVAILABLE;
    }
    
    private String getStatusText() {
        if (hasConflict) return "CONFLIT";
        if (!resource.isDisponible()) return "Indisponible";
        if (isOccupied) return "Occupé";
        return "Disponible";
    }
    
    private String getStatusIcon() {
        if (hasConflict) return "❌";
        if (!resource.isDisponible()) return "⛔";
        if (isOccupied) return "🔶";
        return "✅";
    }
    
    private String getStatusTooltip() {
        if (hasConflict) return "Ressource en conflit - Affectation impossible";
        if (!resource.isDisponible()) return "Ressource indisponible";
        if (isOccupied) return "Ressource actuellement occupée";
        return "Ressource disponible pour affectation";
    }
    
    private String getResourceIcon() {
        return switch (resource.getType()) {
            case GRUE -> "🏗️";
            case CAMION -> "🚛";
            case CHAUFFEUR -> "👷";
            case MAIN_OEUVRE -> "👥";
            case RESSOURCE_GENERIQUE -> "⚙️";
        };
    }
    
    private void checkConflictStatus() {
        // Vérifier les conflits pour la période actuelle (aujourd'hui)
        try {
            InterventionService interventionService = ServiceFactory.getInterventionService();
            LocalDate today = LocalDate.now();
            
            // Simuler une vérification de conflit
            // Dans une vraie application, cela vérifierait les interventions actuelles
            this.isOccupied = false; // À implémenter selon les interventions en cours
            this.hasConflict = false; // À implémenter selon les conflits détectés
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification des conflits: " + e.getMessage());
        }
    }
    
    public void updateConflictStatus(boolean hasConflict, boolean isOccupied) {
        this.hasConflict = hasConflict;
        this.isOccupied = isOccupied;
        
        // Recréer les panels avec les nouveaux statuts
        removeAll();
        
        JPanel iconPanel = createIconPanel();
        add(iconPanel, BorderLayout.WEST);
        
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.EAST);
        
        updateAppearance();
        revalidate();
        repaint();
    }
    
    private void showContextMenu(int x, int y) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        JMenuItem detailsItem = new JMenuItem("Voir les détails");
        detailsItem.addActionListener(e -> showResourceDetails());
        
        JMenuItem occupationItem = new JMenuItem("Voir l'occupation");
        occupationItem.addActionListener(e -> showOccupationDetails());
        
        contextMenu.add(detailsItem);
        if (isOccupied || hasConflict) {
            contextMenu.add(occupationItem);
        }
        
        contextMenu.show(this, x, y);
    }
    
    private void showResourceDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Ressource: ").append(resource.getNom()).append("\n");
        details.append("Type: ").append(resource.getType().getDisplayName()).append("\n");
        details.append("Statut: ").append(getStatusText()).append("\n");
        
        if (resource.getDescription() != null) {
            details.append("Description: ").append(resource.getDescription()).append("\n");
        }
        
        if (resource.getSpecifications() != null) {
            details.append("Spécifications: ").append(resource.getSpecifications()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), 
                                    "Détails de la ressource", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showOccupationDetails() {
        String message = "Période d'occupation et conflits à implémenter";
        if (hasConflict) {
            message = "⚠️ CONFLIT DÉTECTÉ\n\nCette ressource a des affectations qui se chevauchent.";
        } else if (isOccupied) {
            message = "🔶 RESSOURCE OCCUPÉE\n\nCette ressource est actuellement affectée à une intervention.";
        }
        
        JOptionPane.showMessageDialog(this, message, 
                                    "État d'occupation", 
                                    hasConflict ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setupDragAndDrop() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }
    
    // Implémentation DragGestureListener
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        if (!resource.isDisponible() || hasConflict) {
            return; // Ne pas permettre le drag si indisponible ou en conflit
        }
        
        dragging = true;
        updateAppearance();
        
        // Créer un transferable avec les données de la ressource
        String transferData = "RESOURCE:" + resource.getId() + ":" + resource.getNom() + ":" + resource.getType().name();
        StringSelection transferable = new StringSelection(transferData);
        
        // Démarrer le drag
        dragSource.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
    }
    
    // Implémentation DragSourceListener
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        // Feedback visuel lors de l'entrée dans une zone de drop valide
    }
    
    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        // Feedback continu pendant le drag
    }
    
    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // Changement d'action de drop
    }
    
    @Override
    public void dragExit(DragSourceEvent dse) {
        // Sortie d'une zone de drop
    }
    
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragging = false;
        hovered = false;
        updateAppearance();
        
        if (dsde.getDropSuccess()) {
            System.out.println("Drop réussi pour la ressource: " + resource.getNom());
            // Mettre à jour le statut d'occupation si nécessaire
            checkConflictStatus();
        }
    }
    
    public Resource getResource() {
        return resource;
    }
    
    public boolean hasConflict() {
        return hasConflict;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
}
