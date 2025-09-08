package com.materiel.client.view.components;

import com.materiel.client.model.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Carte représentant une ressource avec support drag & drop
 */
public class ResourceCard extends JPanel implements DragGestureListener, DragSourceListener {
    
    private final Resource resource;
    private boolean hovered = false;
    private boolean dragging = false;
    private DragSource dragSource;
    
    public ResourceCard(Resource resource) {
        this.resource = resource;
        initComponents();
        setupDragAndDrop();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 5));
        setPreferredSize(new Dimension(180, 60));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(resource.getType().getColor()), 2, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Icône du type de ressource
        JLabel iconLabel = new JLabel(getResourceIcon());
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        add(iconLabel, BorderLayout.WEST);
        
        // Informations de la ressource
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        
        // Indicateur de disponibilité
        JLabel statusLabel = createStatusLabel();
        add(statusLabel, BorderLayout.EAST);
        
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
                }
            }
        });
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(resource.getNom());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(resource.getType().getDisplayName());
        typeLabel.setFont(typeLabel.getFont().deriveFont(10f));
        typeLabel.setForeground(Color.GRAY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(nameLabel);
        panel.add(typeLabel);
        
        return panel;
    }
    
    private JLabel createStatusLabel() {
        JLabel label = new JLabel();
        if (resource.isDisponible()) {
            label.setText("🟢");
            label.setToolTipText("Disponible");
        } else {
            label.setText("🔴");
            label.setToolTipText("Indisponible");
        }
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        return label;
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
    
    private void setupDragAndDrop() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }
    
    private void updateAppearance() {
        if (hovered && !dragging) {
            setBackground(Color.decode("#F8FAFC"));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(resource.getType().getColor()), 3, true),
                BorderFactory.createEmptyBorder(7, 7, 7, 7)
            ));
        } else {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(resource.getType().getColor()), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
        }
        repaint();
    }
    
    private void showResourceDetails() {
        String details = String.format(
            "Ressource: %s\nType: %s\nStatut: %s\nDescription: %s",
            resource.getNom(),
            resource.getType().getDisplayName(),
            resource.isDisponible() ? "Disponible" : "Indisponible",
            resource.getDescription() != null ? resource.getDescription() : "Aucune description"
        );
        
        JOptionPane.showMessageDialog(this, details, "Détails de la ressource", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Implémentation DragGestureListener
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        if (!resource.isDisponible()) {
            return; // Ne pas permettre le drag des ressources indisponibles
        }
        
        dragging = true;
        updateAppearance();
        
        // Créer un transferable avec les données de la ressource
        String transferData = "RESOURCE:" + resource.getId() + ":" + resource.getNom();
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
            // Le drop a réussi, on pourrait faire quelque chose ici
            System.out.println("Drop réussi pour la ressource: " + resource.getNom());
        }
    }
    
    public Resource getResource() {
        return resource;
    }
} 
