// PlanningPanel.java - Version corrigée pour le drop
package com.materiel.client.view.planning;

import com.materiel.client.model.Resource;
import com.materiel.client.model.Intervention;
import com.materiel.client.model.Client;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.ResourceService;
import com.materiel.client.service.InterventionService;
import com.materiel.client.view.components.ResourceCard;
import com.materiel.client.view.components.InterventionCard;
import com.materiel.client.view.planning.InterventionCreateDialog;
import com.materiel.client.view.planning.PlanningBoard;
import com.materiel.client.view.resources.ResourceEditDialog;
import com.materiel.client.view.planning.layout.LaneLayout;
import com.materiel.client.view.ui.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Panel principal du planning hebdomadaire avec drag & drop intelligent
 */
public class PlanningPanel extends JPanel {
    
    private static final int RESOURCE_PANEL_WIDTH = 200;
    private static final int RESOURCE_COL_WIDTH = 140;
    private static final int DAY_COLUMN_WIDTH = 220;
    private static final int HOUR_ROW_HEIGHT = 100;
    private static final int TILE_HEIGHT = 80;

    private static final Logger log = LoggerFactory.getLogger(PlanningPanel.class);

    private JPanel resourceListPanel;
    private PlanningBoard planningGridPanel;
    private JScrollPane planningScrollPane;
    private CardLayout viewLayout;
    private JPanel viewContainer;
    private DayTimelinePanel dayTimelinePanel;
    private LocalDate currentWeekStart;
    private JLabel weekLabel; // Référence pour la mise à jour
    
    private List<Resource> allResources; // liste complète pour filtrage
    private List<Resource> resources;
    private List<Intervention> interventions;
    private Map<String, DayCell> dayCells; // "resourceId-dayIndex" -> DayCell
    private JComboBox<Object> typeFilterCombo;
    
    public PlanningPanel() {
        currentWeekStart = getStartOfWeek(LocalDate.now());
        allResources = new ArrayList<>();
        resources = new ArrayList<>();
        interventions = new ArrayList<>();
        dayCells = new HashMap<>();
        
        initComponents();
        setupDragAndDrop();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Panel principal avec ressources à gauche et planning à droite
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Liste des ressources à gauche
        resourceListPanel = createResourceListPanel();
        
        // Conteneur de vue avec CardLayout (semaine/jour)
        viewLayout = new CardLayout();
        viewContainer = new JPanel(viewLayout);

        // Vue semaine existante
        planningGridPanel = createPlanningGridPanel();
        viewContainer.add(planningGridPanel, "WEEK");

        // Vue jour timeline (initialisée vide, se mettra à jour après chargement)
        dayTimelinePanel = new DayTimelinePanel(LocalDate.now(), interventions);
        viewContainer.add(new JScrollPane(dayTimelinePanel), "DAY");

        planningScrollPane = new JScrollPane(viewContainer);
        planningScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        planningScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Splitter pour redimensionner
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(resourceListPanel));
        splitPane.setRightComponent(planningScrollPane);
        splitPane.setDividerLocation(RESOURCE_PANEL_WIDTH);
        splitPane.setResizeWeight(0.0);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Navigation semaine
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.setOpaque(false);
        
        JButton prevWeekBtn = new JButton("← Semaine précédente");
        JButton nextWeekBtn = new JButton("Semaine suivante →");
        JButton todayBtn = new JButton("Aujourd'hui");
        JToggleButton dayViewToggle = new JToggleButton("Vue jour");

        prevWeekBtn.addActionListener(e -> navigateWeek(-1));
        nextWeekBtn.addActionListener(e -> navigateWeek(1));
        todayBtn.addActionListener(e -> goToToday());
        dayViewToggle.addActionListener(e -> toggleDayView(dayViewToggle.isSelected()));

        weekLabel = new JLabel(formatWeekRange());
        weekLabel.setFont(weekLabel.getFont().deriveFont(Font.BOLD, 16f));

        // Filtre des types de ressources
        typeFilterCombo = new JComboBox<>();
        typeFilterCombo.addItem("Toutes");
        for (Resource.ResourceType t : Resource.ResourceType.values()) {
            typeFilterCombo.addItem(t);
        }
        typeFilterCombo.addActionListener(e -> applyResourceFilter());
        typeFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Resource.ResourceType type) {
                    value = type.getDisplayName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });


        navigationPanel.add(prevWeekBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(weekLabel);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(nextWeekBtn);
        navigationPanel.add(Box.createHorizontalStrut(20));
        navigationPanel.add(todayBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(dayViewToggle);
        navigationPanel.add(Box.createHorizontalStrut(20));
        navigationPanel.add(typeFilterCombo);

        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);
        
        JButton addResourceBtn = new JButton("+ Ajouter ressource");
        JButton refreshBtn = new JButton("🔄 Actualiser");
        JButton detectConflictsBtn = new JButton("⚠️ Détecter conflits");
        
        addResourceBtn.setBackground(Color.decode("#3B82F6"));
        addResourceBtn.setForeground(Color.WHITE);
        addResourceBtn.addActionListener(e -> showAddResourceDialog());
        
        refreshBtn.addActionListener(e -> refreshPlanning());
        
        detectConflictsBtn.setBackground(Color.decode("#F97316"));
        detectConflictsBtn.setForeground(Color.WHITE);
        detectConflictsBtn.addActionListener(e -> detectAndHighlightConflicts());
        
        actionsPanel.add(detectConflictsBtn);
        actionsPanel.add(addResourceBtn);
        actionsPanel.add(refreshBtn);
        
        panel.add(navigationPanel, BorderLayout.WEST);
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createResourceListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("Ressources");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(10));
        
        return panel;
    }
    
    private PlanningBoard createPlanningGridPanel() {
    	LocalDate semaine = LocalDate.now().with(DayOfWeek.MONDAY);
        PlanningBoard panel = new PlanningBoard(semaine);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        TimelineHeader header = new TimelineHeader(panel.getTimeGridModel(), semaine);

        JScrollPane sp = new JScrollPane(panel);
        sp.setColumnHeaderView(header);
        // Header avec les jours de la semaine
        JPanel headerPanel = createWeekHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);

        // Grid des interventions
        JPanel gridPanel = createInterventionGridPanel();
        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createWeekHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(0, 40));
        panel.setBackground(Color.decode("#F1F5F9"));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        JLabel blank = new JLabel();
        blank.setPreferredSize(new Dimension(RESOURCE_COL_WIDTH, 40));
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(blank, gbc);

        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = currentWeekStart.plusDays(i);
            String dayText = days[i] + " " + dayDate.format(dayFormat);

            JLabel dayLabel = new JLabel(dayText, SwingConstants.CENTER);
            dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD, 12f));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            if (dayDate.equals(LocalDate.now())) {
                dayLabel.setOpaque(true);
                dayLabel.setBackground(Color.decode("#EBF4FF"));
                dayLabel.setForeground(Color.decode("#3B82F6"));
            }

            gbc.gridx = i + 1;
            gbc.weightx = 1.0;
            panel.add(dayLabel, gbc);
        }

        return panel;
    }

    private JPanel createInterventionGridPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        dayCells.clear();

        for (int resourceIndex = 0; resourceIndex < resources.size(); resourceIndex++) {
            GridBagConstraints labelGbc = new GridBagConstraints();
            labelGbc.fill = GridBagConstraints.BOTH;
            labelGbc.gridx = 0;
            labelGbc.gridy = resourceIndex;
            labelGbc.weightx = 0;
            labelGbc.weighty = 1.0;
            JLabel resLabel = new JLabel(resources.get(resourceIndex).getNom());
            resLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            resLabel.setPreferredSize(new Dimension(RESOURCE_COL_WIDTH, HOUR_ROW_HEIGHT));
            panel.add(resLabel, labelGbc);

            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                DayCell dayCell = createDayCell(resourceIndex, dayIndex);
                String key = resourceIndex + "-" + dayIndex;
                dayCells.put(key, dayCell);
                GridBagConstraints cellGbc = new GridBagConstraints();
                cellGbc.fill = GridBagConstraints.BOTH;
                cellGbc.gridx = dayIndex + 1;
                cellGbc.gridy = resourceIndex;
                cellGbc.weightx = 1.0;
                cellGbc.weighty = 1.0;
                panel.add(dayCell, cellGbc);
            }
        }

        return panel;
    }
    
    private DayCell createDayCell(int resourceIndex, int dayIndex) {
        Resource resource = resourceIndex < resources.size() ? resources.get(resourceIndex) : null;
        LocalDate dayDate = currentWeekStart.plusDays(dayIndex);
        
        DayCell cell = new DayCell(resource, dayDate, resourceIndex, dayIndex);
        cell.setPreferredSize(new Dimension(DAY_COLUMN_WIDTH, HOUR_ROW_HEIGHT));
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        // Support du drop - créer un DropTarget pour chaque cellule
        try {
            DropTarget dropTarget = new DropTarget(cell, new InterventionDropTargetListener(cell));
            log.debug("DropTarget créé pour cellule {}-{}", resourceIndex, dayIndex);
        } catch (Exception e) {
            log.error("Erreur création DropTarget", e);
        }
        
        return cell;
    }
    
    private void setupDragAndDrop() {
        // Le drag & drop sera géré dans les composants individuels
        log.debug("Setup drag & drop terminé");
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                log.debug("Chargement des données");
                ResourceService resourceService = ServiceFactory.getResourceService();
                InterventionService interventionService = ServiceFactory.getInterventionService();

                allResources = resourceService.getAllResources();
                interventions = interventionService.getInterventionsByDateRange(currentWeekStart, currentWeekStart.plusDays(6));

                log.debug("{} ressources chargées", allResources.size());
                log.debug("{} interventions chargées", interventions.size());
                applyResourceFilter();
            } catch (Exception e) {
                log.error("Erreur lors du chargement", e);
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des données: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void applyResourceFilter() {
        Object selected = typeFilterCombo != null ? typeFilterCombo.getSelectedItem() : null;
        if (selected instanceof Resource.ResourceType type) {
            resources = allResources.stream()
                    .filter(r -> r.getType() == type)
                    .collect(Collectors.toList());
        } else {
            resources = new ArrayList<>(allResources);
        }

        updateResourceList();
        updatePlanningGrid();
        updateInterventionsDisplay();
    }
    
    private void updateResourceList() {
        resourceListPanel.removeAll();
        
        // Header
        JLabel headerLabel = new JLabel("Ressources");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resourceListPanel.add(headerLabel);
        resourceListPanel.add(Box.createVerticalStrut(10));
        
        // Cartes de ressources
        for (Resource resource : resources) {
            ResourceCard card = new ResourceCard(resource);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            resourceListPanel.add(card);
            resourceListPanel.add(Box.createVerticalStrut(5));
        }
        
        resourceListPanel.add(Box.createVerticalGlue());
        resourceListPanel.revalidate();
        resourceListPanel.repaint();

        log.debug("Liste des ressources mise à jour avec {} éléments", resources.size());
    }
    
    private void updatePlanningGrid() {
        // Recréer le grid avec les nouvelles données
        Component centerComponent = ((BorderLayout) planningGridPanel.getLayout())
            .getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            planningGridPanel.remove(centerComponent);
        }
        
        JPanel newGridPanel = createInterventionGridPanel();
        planningGridPanel.add(newGridPanel, BorderLayout.CENTER);
        
        planningGridPanel.revalidate();
        planningGridPanel.repaint();

        log.debug("Grid de planning mis à jour");
    }
    
    private void updateInterventionsDisplay() {
        // Vider toutes les cellules d'abord
        for (DayCell cell : dayCells.values()) {
            cell.clearInterventions();
        }
        
        // Ajouter les interventions dans les bonnes cellules
        for (Intervention intervention : interventions) {
            if (intervention.getDateDebut() != null && intervention.getRessources() != null) {
                LocalDate interventionDate = intervention.getDateDebut().toLocalDate();
                
                // Vérifier si l'intervention est dans la semaine courante
                if (!interventionDate.isBefore(currentWeekStart) && 
                    !interventionDate.isAfter(currentWeekStart.plusDays(6))) {
                    
                    int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, interventionDate);
                    
                    // Ajouter l'intervention à toutes les cellules des ressources impliquées
                    for (Resource resource : intervention.getRessources()) {
                        int resourceIndex = resources.indexOf(resource);
                        if (resourceIndex >= 0) {
                            String key = resourceIndex + "-" + dayIndex;
                            DayCell cell = dayCells.get(key);
                            if (cell != null) {
                                cell.addIntervention(intervention);
                            }
                        }
                    }
                }
            }
        }
        
        // Détecter et afficher les conflits
        detectAndHighlightConflicts();

        // Mettre à jour la vue jour
        if (dayTimelinePanel != null) {
            dayTimelinePanel.setDate(LocalDate.now(), interventions);
        }
    }
    
    private void detectAndHighlightConflicts() {
        // Réinitialiser les couleurs
        for (DayCell cell : dayCells.values()) {
            cell.setConflict(false);
        }
        
        // Détecter les conflits
        try {
            InterventionService interventionService = ServiceFactory.getInterventionService();

            for (Intervention intervention : interventions) {
                if (interventionService.hasConflict(intervention)) {
                    markInterventionAsConflicted(intervention);
                }
            }
        } catch (Exception e) {
            log.error("Erreur détection conflits", e);
        }
        
        repaint();
    }
    
    private void markInterventionAsConflicted(Intervention intervention) {
        if (intervention.getDateDebut() != null && intervention.getRessources() != null) {
            LocalDate interventionDate = intervention.getDateDebut().toLocalDate();
            
            if (!interventionDate.isBefore(currentWeekStart) && 
                !interventionDate.isAfter(currentWeekStart.plusDays(6))) {
                
                int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, interventionDate);
                
                for (Resource resource : intervention.getRessources()) {
                    int resourceIndex = resources.indexOf(resource);
                    if (resourceIndex >= 0) {
                        String key = resourceIndex + "-" + dayIndex;
                        DayCell cell = dayCells.get(key);
                        if (cell != null) {
                            cell.setConflict(true);
                        }
                    }
                }
            }
        }
    }
    
    public void refreshPlanning() {
        loadData();
    }
    
    private void navigateWeek(int direction) {
        currentWeekStart = currentWeekStart.plusWeeks(direction);
        updateWeekDisplay();
        loadData();
    }
    
    private void goToToday() {
        currentWeekStart = getStartOfWeek(LocalDate.now());
        updateWeekDisplay();
        loadData();
    }

    private void toggleDayView(boolean dayView) {
        if (dayView) {
            dayTimelinePanel.setDate(LocalDate.now(), interventions);
            viewLayout.show(viewContainer, "DAY");
        } else {
            viewLayout.show(viewContainer, "WEEK");
        }
    }
    
    private void updateWeekDisplay() {
        if (weekLabel != null) {
            weekLabel.setText(formatWeekRange());
        }
        
        // Mettre à jour le header avec les nouvelles dates
        Component headerComponent = ((BorderLayout) planningGridPanel.getLayout())
            .getLayoutComponent(BorderLayout.NORTH);
        if (headerComponent != null) {
            planningGridPanel.remove(headerComponent);
            JPanel newHeaderPanel = createWeekHeaderPanel();
            planningGridPanel.add(newHeaderPanel, BorderLayout.NORTH);
            planningGridPanel.revalidate();
            planningGridPanel.repaint();
        }
    }
    
    private void showAddResourceDialog() {
        ResourceEditDialog dialog = new ResourceEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                Resource newResource = dialog.getResource();
                ResourceService resourceService = ServiceFactory.getResourceService();
                resourceService.saveResource(newResource);
                
                // Rafraîchir le planning pour inclure la nouvelle ressource
                refreshPlanning();
                
                JOptionPane.showMessageDialog(this,
                    "Ressource créée avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création de la ressource: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private LocalDate getStartOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
    
    private String formatWeekRange() {
        LocalDate endDate = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return currentWeekStart.format(formatter) + " - " + endDate.format(formatter);
    }
    
    /**
     * Cellule représentant un jour pour une ressource
     */
    private class DayCell extends JPanel {
        private final Resource resource;
        private final LocalDate date;
        private final int resourceIndex;
        private final int dayIndex;
        private final List<InterventionCard> interventionCards;
        private boolean hasConflict = false;

        public DayCell(Resource resource, LocalDate date, int resourceIndex, int dayIndex) {
            this.resource = resource;
            this.date = date;
            this.resourceIndex = resourceIndex;
            this.dayIndex = dayIndex;
            this.interventionCards = new ArrayList<>();
            setLayout(null);
            setBackground(Color.WHITE);
            updateAppearance();
        }

        public void addIntervention(Intervention intervention) {
            InterventionCard card = new InterventionCard(intervention);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension size = new Dimension(DAY_COLUMN_WIDTH - 10, TILE_HEIGHT);
            card.setPreferredSize(size);
            card.setMaximumSize(size);

            interventionCards.add(card);
            interventionCards.sort((a, b) -> {
                LocalDateTime sa = a.getIntervention().getDateDebut();
                LocalDateTime sb = b.getIntervention().getDateDebut();
                if (sa == null || sb == null) return 0;
                return sb.compareTo(sa);
            });

            applyLayout();
        }

        private void applyLayout() {
            removeAll();
            List<Intervention> ints = interventionCards.stream()
                    .map(InterventionCard::getIntervention)
                    .collect(Collectors.toList());
            final int gutter = 2;
            final int available = DAY_COLUMN_WIDTH - 10;
            Map<Intervention, LaneLayout.Lane> map = LaneLayout.computeLanes(
                    ints,
                    new LaneLayout.StartEnd<Intervention>() {
                        @Override public LocalDateTime start(Intervention t) { return t.getDateDebut(); }
                        @Override public LocalDateTime end(Intervention t) { return t.getDateFin(); }
                    },
                    available);
            int y = 0;
            for (InterventionCard c : interventionCards) {
                LaneLayout.Lane lane = map.get(c.getIntervention());
                int width = available;
                int xOffset = 0;
                if (lane != null) {
                    width = Math.max(UIConstants.MIN_TILE_WIDTH,
                            (available - (lane.count - 1) * gutter) / lane.count);
                    xOffset = lane.index * (width + gutter);
                }
                int height = Math.max(UIConstants.MIN_TILE_HEIGHT, TILE_HEIGHT);
                c.setBounds(xOffset, y, width, height);
                add(c);
                y += height + gutter;
            }

            revalidate();
            repaint();
        }

        public void clearInterventions() {
            for (InterventionCard card : interventionCards) {
                remove(card);
            }
            interventionCards.clear();
            removeAll();
            revalidate();
            repaint();
        }

        public void setConflict(boolean conflict) {
            this.hasConflict = conflict;
            updateAppearance();
        }

        private void updateAppearance() {
            if (hasConflict) {
                setBackground(Color.decode("#FEF2F2")); // Rouge très clair
                setBorder(BorderFactory.createLineBorder(Color.decode("#EF4444"), 2));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        }

        public Resource getResource() { return resource; }
        public LocalDate getDate() { return date; }
        public int getResourceIndex() { return resourceIndex; }
        public int getDayIndex() { return dayIndex; }
        public List<InterventionCard> getInterventionCards() { return interventionCards; }
    }
    
    /**
     * Listener pour le drop d'interventions avec logique intelligente
     */
    private class InterventionDropTargetListener implements DropTargetListener {
        private final DayCell targetCell;
        
        public InterventionDropTargetListener(DayCell targetCell) {
            this.targetCell = targetCell;
        }
        
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            log.debug("Drag enter sur cellule {}-{}", targetCell.getResourceIndex(), targetCell.getDayIndex());
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            // Feedback visuel
            targetCell.setBackground(Color.decode("#EBF4FF"));
        }
        
        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            // Feedback continu pendant le drag
        }
        
        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            // Non utilisé
        }
        
        @Override
        public void dragExit(DropTargetEvent dte) {
            log.debug("Drag exit");
            // Restaurer l'apparence normale
            targetCell.updateAppearance();
        }
        
        @Override
        public void drop(DropTargetDropEvent dtde) {
            log.debug("Drop détecté");
            try {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                Transferable transferable = dtde.getTransferable();
                if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    log.debug("Données reçues: {}", data);

                    // Parser les données : "RESOURCE:id:nom:type" ou "INTERVENTION:id"
                    String[] parts = data.split(":");
                    if (parts.length >= 2 && "INTERVENTION".equals(parts[0])) {
                        Long interventionId = Long.parseLong(parts[1]);
                        log.debug("Tentative de déplacement pour intervention ID: {}", interventionId);

                        handleInterventionDrop(interventionId);
                        dtde.getDropTargetContext().dropComplete(true);
                        log.info("Intervention déplacée avec succès");
                    } else if (parts.length >= 3 && "RESOURCE".equals(parts[0])) {
                        Long resourceId = Long.parseLong(parts[1]);
                        log.debug("Tentative de drop pour ressource ID: {}", resourceId);

                        handleResourceDrop(resourceId, dtde.getLocation());
                        dtde.getDropTargetContext().dropComplete(true);
                        log.info("Drop traité avec succès");
                    } else {
                        log.error("Format de données invalide: {}", data);
                        dtde.getDropTargetContext().dropComplete(false);
                    }
                } else {
                    log.error("DataFlavor non supporté");
                    dtde.getDropTargetContext().dropComplete(false);
                }
            } catch (Exception e) {
                log.error("Erreur durant le drop", e);
                dtde.getDropTargetContext().dropComplete(false);
            } finally {
                // Restaurer l'apparence
                targetCell.updateAppearance();
            }
        }
        
        private void handleResourceDrop(Long resourceId, Point dropPoint) {
            SwingUtilities.invokeLater(() -> {
                try {
                    log.debug("Traitement du drop pour ressource ID: {}", resourceId);

                    Resource droppedResource = resources.stream()
                            .filter(r -> r.getId().equals(resourceId))
                            .findFirst()
                            .orElse(null);

                    if (droppedResource == null) {
                        log.error("Ressource non trouvée avec ID: {}", resourceId);
                        JOptionPane.showMessageDialog(PlanningPanel.this,
                            "Ressource non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    log.debug("Ressource trouvée: {}", droppedResource.getNom());
                    log.debug("Date cible: {}", targetCell.getDate());

                    Component comp = targetCell.getComponentAt(dropPoint);
                    InterventionCard card = (InterventionCard) SwingUtilities.getAncestorOfClass(InterventionCard.class, comp);

                    if (card != null) {
                        log.debug("Drop sur intervention existante");
                        addResourceToExistingIntervention(card.getIntervention(), droppedResource);
                    } else {
                        log.debug("Drop hors intervention, création d'une nouvelle");
                        createNewIntervention(targetCell.getDate(), droppedResource);
                    }

                } catch (Exception e) {
                    log.error("Erreur handleResourceDrop", e);
                    JOptionPane.showMessageDialog(PlanningPanel.this,
                        "Erreur lors de la création de l'intervention: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        private void handleInterventionDrop(Long interventionId) {
            SwingUtilities.invokeLater(() -> {
                try {
                    log.debug("Déplacement de l'intervention ID: {}", interventionId);

                    Intervention movedIntervention = interventions.stream()
                            .filter(i -> i.getId().equals(interventionId))
                            .findFirst()
                            .orElse(null);

                    if (movedIntervention == null) {
                        log.error("Intervention non trouvée avec ID: {}", interventionId);
                        JOptionPane.showMessageDialog(PlanningPanel.this,
                            "Intervention non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    LocalDate targetDate = targetCell.getDate();
                    LocalDateTime newStart = LocalDateTime.of(targetDate, movedIntervention.getDateDebut().toLocalTime());
                    LocalDateTime newEnd = LocalDateTime.of(targetDate, movedIntervention.getDateFin().toLocalTime());

                    movedIntervention.setDateDebut(newStart);
                    movedIntervention.setDateFin(newEnd);

                    InterventionService interventionService = ServiceFactory.getInterventionService();
                    interventionService.saveIntervention(movedIntervention);

                    refreshPlanning();

                    JOptionPane.showMessageDialog(PlanningPanel.this,
                        "Intervention déplacée au " + targetDate,
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    log.error("Erreur déplacement intervention", e);
                    JOptionPane.showMessageDialog(PlanningPanel.this,
                        "Erreur lors du déplacement: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        
        private void addResourceToExistingIntervention(Intervention intervention, Resource newResource) {
            // Vérifier si la ressource n'est pas déjà dans l'intervention
            if (intervention.getRessources() != null &&
                intervention.getRessources().stream().anyMatch(r -> r.getId().equals(newResource.getId()))) {
                JOptionPane.showMessageDialog(PlanningPanel.this,
                    "Cette ressource est déjà affectée à cette intervention",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Ajouter la ressource
            if (intervention.getRessources() == null) {
                intervention.setRessources(new ArrayList<>());
            }
            intervention.getRessources().add(newResource);
            
            // Sauvegarder
            try {
                InterventionService interventionService = ServiceFactory.getInterventionService();
                interventionService.saveIntervention(intervention);

                // Rafraîchir l'affichage
                refreshPlanning();

                JOptionPane.showMessageDialog(PlanningPanel.this,
                    "Ressource " + newResource.getNom() + " ajoutée à l'intervention existante",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                log.error("Erreur sauvegarde intervention", e);
                JOptionPane.showMessageDialog(PlanningPanel.this,
                    "Erreur lors de la sauvegarde: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void createNewIntervention(LocalDate date, Resource resource) {
            log.debug("Création d'une nouvelle intervention");
            
            // CORRECTION: Créer des heures par défaut plus sensées
            LocalDateTime dateDebut = date.atTime(8, 0); // 8h00
            LocalDateTime dateFin = date.atTime(17, 0);   // 17h00
            
            // Afficher le dialogue de création d'intervention
            InterventionCreateDialog dialog = new InterventionCreateDialog(
                (Frame) SwingUtilities.getWindowAncestor(PlanningPanel.this), 
                resource,
                dateDebut, 
                dateFin, 
                false
            );
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Intervention newIntervention = dialog.getIntervention();
                log.debug("Nouvelle intervention confirmée: {}", newIntervention.getTitre());
                
                // Sauvegarder
                try {
                    InterventionService interventionService = ServiceFactory.getInterventionService();
                    
                    // Vérifier les conflits avant sauvegarde
                    if (interventionService.hasConflict(newIntervention)) {
                        int result = JOptionPane.showConfirmDialog(PlanningPanel.this,
                            "⚠️ Conflit détecté !\n\nUne ou plusieurs ressources sont déjà affectées sur cette période.\n" +
                            "Voulez-vous continuer malgré tout ?",
                            "Conflit de ressources",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                        
                        if (result != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    
                    interventionService.saveIntervention(newIntervention);
                    log.info("Intervention sauvegardée avec succès");
                    
                    // Rafraîchir l'affichage
                    refreshPlanning();
                    
                    JOptionPane.showMessageDialog(PlanningPanel.this,
                        "Intervention créée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    log.error("Erreur création intervention", e);
                    JOptionPane.showMessageDialog(PlanningPanel.this,
                        "Erreur lors de la sauvegarde: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                log.debug("Création d'intervention annulée");
            }
        }
    }

    /** Change the active time scale for the planning board. */
    public void setTimeScale(int minutesPerCell) {
        if (planningGridPanel != null) {
//            planningGridPanel.setTimeScale(minutesPerCell);
        }
    }

    /** Enable or disable multi-selection on the board. */
    public void enableMultiSelection(boolean enable) {
        if (planningGridPanel != null) {
//            planningGridPanel.enableMultiSelection(enable);
        }
    }

    /** Apply current snap increment to a time value. */
//    public LocalDateTime applySnap(LocalDateTime time) {
//        return planningGridPanel != null ? planningGridPanel.applySnap(time) : time;
//    }
}