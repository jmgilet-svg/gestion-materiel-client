package com.materiel.client.view.components;

import com.materiel.client.model.Intervention;
import com.materiel.client.model.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Carte d'intervention améliorée avec design riche et informations complètes
 */
public class InterventionCard extends JPanel implements DragGestureListener, DragSourceListener {
    
    private final Intervention intervention;
    private boolean hovered = false;
    private boolean selected = false;
    private boolean highlighted = false; // Pour le feedback DnD
    private boolean dragging = false;
    private DragSource dragSource;
    
    // Constantes de design
    private static final Color BACKGROUND_NORMAL = Color.WHITE;
    private static final Color BACKGROUND_HOVER = Color.decode("#F8FAFC");
    private static final Color BACKGROUND_SELECTED = Color.decode("#EBF4FF");
    private static final Color BACKGROUND_HIGHLIGHT = Color.decode("#FEF3C7"); // Jaune clair pour DnD
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font DETAIL_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    private static final Font TIME_FONT = new Font("Segoe UI", Font.BOLD, 10);
    
    public InterventionCard(Intervention intervention) {
        this.intervention = intervention;
        initComponents();
        setupEventHandlers();
        setupDragAndDrop();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 4));
        setOpaque(true);
        setPreferredSize(new Dimension(160, 120)); // Plus haute pour plus d'infos
        setMinimumSize(new Dimension(140, 100));
        setMaximumSize(new Dimension(200, 140));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        updateAppearance();
        
        // Header avec statut et temps
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenu principal
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer avec ressources
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Pastille de statut + titre
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        // Pastille de statut avec tooltip
        JLabel statusLabel = new JLabel("●");
        statusLabel.setForeground(Color.decode(intervention.getStatut().getColor()));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setToolTipText(intervention.getStatut().getDisplayName());
        
        // Titre de l'intervention
        JLabel titleLabel = new JLabel(truncateText(intervention.getTitre(), 18));
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.decode("#1F2937"));
        
        leftPanel.add(statusLabel);
        leftPanel.add(Box.createHorizontalStrut(4));
        leftPanel.add(titleLabel);
        
        // Horaires à droite
        JPanel rightPanel = createTimePanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTimePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
            String heureDebut = intervention.getDateDebut().format(timeFormatter);
            String heureFin = intervention.getDateFin().format(timeFormatter);
            
            JLabel timeLabel = new JLabel(heureDebut + " - " + heureFin);
            timeLabel.setFont(TIME_FONT);
            timeLabel.setForeground(Color.decode("#6B7280"));
            timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            // Durée
            long dureeMinutes = java.time.Duration.between(
                intervention.getDateDebut(), intervention.getDateFin()).toMinutes();
            String dureeText = formatDuree(dureeMinutes);
            
            JLabel dureeLabel = new JLabel("(" + dureeText + ")");
            dureeLabel.setFont(DETAIL_FONT);
            dureeLabel.setForeground(Color.decode("#9CA3AF"));
            dureeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            panel.add(timeLabel);
            panel.add(dureeLabel);
        }
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Client avec icône
        if (intervention.getClient() != null) {
            JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
            clientPanel.setOpaque(false);
            
            JLabel clientIcon = new JLabel("👤");
            clientIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            
            JLabel clientLabel = new JLabel(truncateText(intervention.getClient().getNom(), 20));
            clientLabel.setFont(SUBTITLE_FONT);
            clientLabel.setForeground(Color.decode("#374151"));
            
            clientPanel.add(clientIcon);
            clientPanel.add(clientLabel);
            panel.add(clientPanel);
        }
        
        // Adresse d'intervention
        if (intervention.getAdresseIntervention() != null && 
            !intervention.getAdresseIntervention().trim().isEmpty()) {
            
            JPanel adressePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
            adressePanel.setOpaque(false);
            
            JLabel adresseIcon = new JLabel("📍");
            adresseIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            
            JLabel adresseLabel = new JLabel(truncateText(intervention.getAdresseIntervention(), 22));
            adresseLabel.setFont(DETAIL_FONT);
            adresseLabel.setForeground(Color.decode("#6B7280"));
            
            adressePanel.add(adresseIcon);
            adressePanel.add(adresseLabel);
            panel.add(adressePanel);
        }
        
        // Description si disponible
        if (intervention.getDescription() != null && 
            !intervention.getDescription().trim().isEmpty()) {
            
            JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
            descPanel.setOpaque(false);
            
            JLabel descIcon = new JLabel("📝");
            descIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            
            JLabel descLabel = new JLabel(truncateText(intervention.getDescription(), 20));
            descLabel.setFont(DETAIL_FONT);
            descLabel.setForeground(Color.decode("#6B7280"));
            
            descPanel.add(descIcon);
            descPanel.add(descLabel);
            panel.add(descPanel);
        }
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Ressources affectées
        if (intervention.getRessources() != null && !intervention.getRessources().isEmpty()) {
            JPanel resourcesPanel = createResourcesPanel();
            panel.add(resourcesPanel, BorderLayout.CENTER);
        }
        
        // Notes importantes
        if (intervention.getNotes() != null && !intervention.getNotes().trim().isEmpty()) {
            JLabel notesIcon = new JLabel("💬");
            notesIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            notesIcon.setToolTipText("Notes: " + intervention.getNotes());
            panel.add(notesIcon, BorderLayout.EAST);
        }
        
        return panel;
    }
    
    private JPanel createResourcesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);
        
        // Icône ressources
        JLabel resourceIcon = new JLabel("🔧");
        resourceIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        panel.add(resourceIcon);
        
        // Compter par type de ressource
        java.util.Map<Resource.ResourceType, Long> resourceCounts = intervention.getRessources().stream()
                .collect(Collectors.groupingBy(Resource::getType, Collectors.counting()));
        
        StringBuilder resourceText = new StringBuilder();
        for (java.util.Map.Entry<Resource.ResourceType, Long> entry : resourceCounts.entrySet()) {
            if (resourceText.length() > 0) resourceText.append(", ");
            
            String icon = getResourceTypeIcon(entry.getKey());
            resourceText.append(icon).append(entry.getValue());
        }
        
        JLabel resourceLabel = new JLabel(resourceText.toString());
        resourceLabel.setFont(DETAIL_FONT);
        resourceLabel.setForeground(Color.decode("#6B7280"));
        
        // Tooltip avec détail des ressources
        String tooltip = "<html><b>Ressources affectées:</b><br>" +
                intervention.getRessources().stream()
                        .map(r -> "• " + r.getNom() + " (" + r.getType().getDisplayName() + ")")
                        .collect(Collectors.joining("<br>")) +
                "</html>";
        resourceLabel.setToolTipText(tooltip);
        
        panel.add(resourceLabel);
        
        return panel;
    }
    
    private String getResourceTypeIcon(Resource.ResourceType type) {
        return switch (type) {
            case GRUE -> "🏗️";
            case CAMION -> "🚛";
            case CHAUFFEUR -> "👷";
            case MAIN_OEUVRE -> "👥";
            case RESSOURCE_GENERIQUE -> "⚙️";
        };
    }
    
    private String formatDuree(long minutes) {
        if (minutes < 60) {
            return minutes + "min";
        } else {
            long heures = minutes / 60;
            long minutesRestantes = minutes % 60;
            if (minutesRestantes == 0) {
                return heures + "h";
            } else {
                return heures + "h" + minutesRestantes + "min";
            }
        }
    }
    
    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!highlighted) {
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
                if (e.getClickCount() == 1) {
                    selected = !selected;
                    updateAppearance();
                    
                    // Publier événement de sélection
                    fireInterventionSelected();
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

        if (dragging) {
            backgroundColor = BACKGROUND_HOVER;
            borderColor = Color.decode("#3B82F6");
            borderWidth = 2;
        } else if (highlighted) {
            backgroundColor = BACKGROUND_HIGHLIGHT;
            borderColor = Color.decode("#F59E0B"); // Orange pour highlight DnD
            borderWidth = 3;
        } else if (selected) {
            backgroundColor = BACKGROUND_SELECTED;
            borderColor = Color.decode("#3B82F6");
            borderWidth = 2;
        } else if (hovered) {
            backgroundColor = BACKGROUND_HOVER;
            borderColor = Color.decode(intervention.getStatut().getColor());
            borderWidth = 2;
        } else {
            backgroundColor = BACKGROUND_NORMAL;
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

    private void setupDragAndDrop() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }

    // Implémentation DragGestureListener
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        if (intervention.getId() == null) {
            return;
        }
        dragging = true;
        updateAppearance();

        String transferData = "INTERVENTION:" + intervention.getId();
        StringSelection transferable = new StringSelection(transferData);

        try {
            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
        } catch (Exception e) {
            dragging = false;
            updateAppearance();
            e.printStackTrace();
        }
    }

    // Implémentation DragSourceListener
    @Override
    public void dragEnter(DragSourceDragEvent dsde) { }

    @Override
    public void dragOver(DragSourceDragEvent dsde) { }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) { }

    @Override
    public void dragExit(DragSourceEvent dse) { }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragging = false;
        updateAppearance();
    }
    
    /**
     * Mettre en évidence la carte pour le feedback DnD
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        updateAppearance();
    }
    
    /**
     * Vérifier si cette intervention peut accepter une ressource
     */
    public boolean canAcceptResource(Resource resource) {
        if (intervention.getRessources() == null) {
            return true;
        }
        
        // Vérifier que la ressource n'est pas déjà dans l'intervention
        return intervention.getRessources().stream()
                .noneMatch(r -> r.getId().equals(resource.getId()));
    }
    
    /**
     * Ajouter visuellement une ressource (feedback avant confirmation)
     */
    public void previewAddResource(Resource resource) {
        setHighlighted(true);
        
        // Changer le tooltip temporairement
        String currentTooltip = getToolTipText();
        setToolTipText("<html><b>Ajouter:</b> " + resource.getNom() + 
                      "<br><i>Relâchez pour confirmer</i></html>");
        
        // Restaurer après un délai
        Timer timer = new Timer(2000, e -> {
            setToolTipText(currentTooltip);
            if (!hovered && !selected) {
                setHighlighted(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void fireInterventionSelected() {
        // TODO: Publier un événement via EventBus si nécessaire
        System.out.println("Intervention sélectionnée: " + intervention.getTitre());
    }
    
    private void showInterventionDetails() {
        String details = formatInterventionDetails();
        
        // Utiliser un dialogue plus riche
        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setBackground(Color.decode("#F9FAFB"));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
                                    "📋 Détails - " + intervention.getTitre(), 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String formatInterventionDetails() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("🔹 INTERVENTION: ").append(intervention.getTitre()).append("\n");
        sb.append("📊 Statut: ").append(intervention.getStatut().getDisplayName()).append("\n\n");
        
        if (intervention.getClient() != null) {
            sb.append("👤 Client: ").append(intervention.getClient().getNom()).append("\n");
        }
        
        if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            sb.append("🕐 Début: ").append(intervention.getDateDebut().format(formatter)).append("\n");
            sb.append("🕑 Fin: ").append(intervention.getDateFin().format(formatter)).append("\n");
            
            long duree = java.time.Duration.between(intervention.getDateDebut(), intervention.getDateFin()).toMinutes();
            sb.append("⏱️ Durée: ").append(formatDuree(duree)).append("\n\n");
        }
        
        if (intervention.getAdresseIntervention() != null) {
            sb.append("📍 Adresse: ").append(intervention.getAdresseIntervention()).append("\n\n");
        }
        
        if (intervention.getRessources() != null && !intervention.getRessources().isEmpty()) {
            sb.append("🔧 Ressources affectées:\n");
            for (Resource resource : intervention.getRessources()) {
                sb.append("  • ").append(resource.getNom())
                  .append(" (").append(resource.getType().getDisplayName()).append(")\n");
            }
            sb.append("\n");
        }
        
        if (intervention.getDescription() != null && !intervention.getDescription().trim().isEmpty()) {
            sb.append("📝 Description:\n").append(intervention.getDescription()).append("\n\n");
        }
        
        if (intervention.getNotes() != null && !intervention.getNotes().trim().isEmpty()) {
            sb.append("💬 Notes:\n").append(intervention.getNotes()).append("\n");
        }
        
        return sb.toString();
    }
    
    private void showContextMenu(int x, int y) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        // Voir détails
        JMenuItem detailsItem = new JMenuItem("📋 Voir les détails");
        detailsItem.addActionListener(e -> showInterventionDetails());
        contextMenu.add(detailsItem);
        
        contextMenu.addSeparator();
        
        // Modifier
        JMenuItem editItem = new JMenuItem("✏️ Modifier");
        editItem.addActionListener(e -> editIntervention());
        contextMenu.add(editItem);
        
        // Dupliquer
        JMenuItem duplicateItem = new JMenuItem("📋 Dupliquer");
        duplicateItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        duplicateItem.addActionListener(e -> duplicateIntervention());
        contextMenu.add(duplicateItem);
        
        contextMenu.addSeparator();
        
        // Changer statut
        JMenu statutMenu = new JMenu("🔄 Changer statut");
        for (Intervention.StatutIntervention statut : Intervention.StatutIntervention.values()) {
            if (statut != intervention.getStatut()) {
                JMenuItem statutItem = new JMenuItem("● " + statut.getDisplayName());
                statutItem.setForeground(Color.decode(statut.getColor()));
                statutItem.addActionListener(e -> changeStatut(statut));
                statutMenu.add(statutItem);
            }
        }
        contextMenu.add(statutMenu);
        
        contextMenu.addSeparator();
        
        // Transformer en devis
        JMenuItem transformItem = new JMenuItem("💰 Transformer en devis");
        transformItem.addActionListener(e -> transformToDevis());
        contextMenu.add(transformItem);
        
        contextMenu.addSeparator();
        
        // Supprimer
        JMenuItem deleteItem = new JMenuItem("🗑️ Supprimer");
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
            case java.awt.event.KeyEvent.VK_SPACE:
                selected = !selected;
                updateAppearance();
                fireInterventionSelected();
                break;
        }
    }
    
    // Méthodes d'action (à implémenter selon les besoins)
    private void editIntervention() {
        // TODO: Ouvrir le dialogue d'édition
        JOptionPane.showMessageDialog(this, "🚧 Édition de l'intervention à implémenter");
    }
    
    private void duplicateIntervention() {
        // TODO: Dupliquer l'intervention
        JOptionPane.showMessageDialog(this, "📋 Duplication: " + intervention.getTitre());
    }
    
    private void changeStatut(Intervention.StatutIntervention nouveauStatut) {
        // TODO: Changer le statut
        JOptionPane.showMessageDialog(this, "🔄 Changement de statut vers: " + nouveauStatut.getDisplayName());
    }
    
    private void transformToDevis() {
        // TODO: Transformer en devis
        JOptionPane.showMessageDialog(this, "💰 Transformation en devis pour: " + intervention.getTitre());
    }
    
    private void deleteIntervention() {
        int result = JOptionPane.showConfirmDialog(this,
            "❓ Voulez-vous vraiment supprimer cette intervention ?\n\n" +
            "📋 " + intervention.getTitre() + "\n" +
            "👤 " + (intervention.getClient() != null ? intervention.getClient().getNom() : "Sans client") + "\n\n" +
            "⚠️ Cette action est irréversible.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            // TODO: Supprimer l'intervention
            JOptionPane.showMessageDialog(this, "🗑️ Intervention supprimée: " + intervention.getTitre());
        }
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    // Getters
    public Intervention getIntervention() { return intervention; }
    public boolean isSelected() { return selected; }
    public boolean isHighlighted() { return highlighted; }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateAppearance();
    }
}