package com.materiel.client.view.resources;

import com.materiel.client.model.Resource;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.util.ColorUtils;
import com.materiel.client.service.ResourceService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel de liste des ressources avec fonctionnalités CRUD
 */
public class ResourceListPanel extends JPanel {
    
    private JTable resourceTable;
    private ResourceTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton toggleStatusButton;
    private JComboBox<Resource.ResourceType> typeFilter;
    
    private List<Resource> resourcesList;
    private List<Resource> filteredResourcesList;
    
    public ResourceListPanel() {
        resourcesList = new ArrayList<>();
        filteredResourcesList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Table des ressources au centre
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel d'actions à droite
        JPanel actionsPanel = createActionsPanel();
        add(actionsPanel, BorderLayout.EAST);
    }
    
    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Titre et filtres
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏗️ Gestion des Ressources");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        JLabel filterLabel = new JLabel("Filtrer par type:");
        typeFilter = new JComboBox<>();
        typeFilter.addItem(null); // "Tous"
        for (Resource.ResourceType type : Resource.ResourceType.values()) {
            typeFilter.addItem(type);
        }
        typeFilter.setRenderer(new TypeComboBoxRenderer());
        typeFilter.addActionListener(e -> filterResources());
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createHorizontalStrut(30));
        leftPanel.add(filterLabel);
        leftPanel.add(typeFilter);
        
        // Boutons d'action
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        addButton = new JButton("+ Nouvelle Ressource");
        addButton.setBackground(Color.decode("#8B5CF6"));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(160, 35));
        addButton.addActionListener(e -> createNewResource());
        
        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setPreferredSize(new Dimension(110, 35));
        refreshButton.addActionListener(e -> refreshData());
        
        rightPanel.add(refreshButton);
        rightPanel.add(addButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Modèle de table
        tableModel = new ResourceTableModel();
        resourceTable = new JTable(tableModel);
        
        // Configuration de la table
        setupTable();
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(resourceTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceTable.setRowHeight(40);
        resourceTable.setShowGrid(true);
        resourceTable.setGridColor(Color.decode("#E5E7EB"));
        resourceTable.getTableHeader().setBackground(Color.decode("#F9FAFB"));
        resourceTable.getTableHeader().setFont(resourceTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Configuration des colonnes
        resourceTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Nom
        resourceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Type
        resourceTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Description
        resourceTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Statut
        
        // Renderer personnalisé pour le type
        resourceTable.getColumnModel().getColumn(1).setCellRenderer(new TypeCellRenderer());
        
        // Renderer personnalisé pour le statut
        resourceTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        // Double-clic pour éditer
        resourceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedResource();
                }
            }
        });
        
        // Mise à jour des boutons selon la sélection
        resourceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        panel.setPreferredSize(new Dimension(170, 0));
        
        JLabel actionsLabel = new JLabel("Actions");
        actionsLabel.setFont(actionsLabel.getFont().deriveFont(Font.BOLD, 14f));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        editButton = createActionButton("✏️ Modifier", this::editSelectedResource);
        toggleStatusButton = createActionButton("🔄 Changer statut", this::toggleResourceStatus);
        deleteButton = createActionButton("🗑️ Supprimer", this::deleteSelectedResource);
        
        // Désactiver les boutons par défaut
        editButton.setEnabled(false);
        toggleStatusButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(toggleStatusButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(deleteButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createActionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 35));
        button.setPreferredSize(new Dimension(150, 35));
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                ResourceService resourceService = ServiceFactory.getResourceService();
                resourcesList = resourceService.getAllResources();
                
                filterResources();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des ressources: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void filterResources() {
        Resource.ResourceType selectedType = (Resource.ResourceType) typeFilter.getSelectedItem();
        
        if (selectedType == null) {
            filteredResourcesList = new ArrayList<>(resourcesList);
        } else {
            filteredResourcesList = resourcesList.stream()
                    .filter(r -> r.getType() == selectedType)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        tableModel.fireTableDataChanged();
        updateButtonStates();
    }
    
    private void createNewResource() {
        ResourceEditDialog dialog = new ResourceEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                Resource newResource = dialog.getResource();
                ResourceService resourceService = ServiceFactory.getResourceService();
                resourceService.saveResource(newResource);
                refreshData();
                
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
    
    private void editSelectedResource() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow >= 0) {
            Resource resource = filteredResourcesList.get(selectedRow);
            
            ResourceEditDialog dialog = new ResourceEditDialog((Frame) SwingUtilities.getWindowAncestor(this), resource);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                try {
                    Resource updatedResource = dialog.getResource();
                    ResourceService resourceService = ServiceFactory.getResourceService();
                    resourceService.saveResource(updatedResource);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Ressource modifiée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de la ressource: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void toggleResourceStatus() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow >= 0) {
            Resource resource = filteredResourcesList.get(selectedRow);
            
            String newStatus = resource.isDisponible() ? "indisponible" : "disponible";
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous rendre cette ressource " + newStatus + " ?\n" +
                "Ressource: " + resource.getNom(),
                "Changement de statut",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    resource.setDisponible(!resource.isDisponible());
                    
                    ResourceService resourceService = ServiceFactory.getResourceService();
                    resourceService.saveResource(resource);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Statut de la ressource mis à jour avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la mise à jour du statut: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSelectedResource() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow >= 0) {
            Resource resource = filteredResourcesList.get(selectedRow);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cette ressource ?\n" +
                "Ressource: " + resource.getNom() + "\n" +
                "Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    ResourceService resourceService = ServiceFactory.getResourceService();
                    resourceService.deleteResource(resource.getId());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Ressource supprimée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = resourceTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        toggleStatusButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            Resource selectedResource = filteredResourcesList.get(resourceTable.getSelectedRow());
            String statusText = selectedResource.isDisponible() ? "🔴 Indisponibiliser" : "🟢 Rendre disponible";
            toggleStatusButton.setText(statusText);
        }
    }
    
    /**
     * Modèle de table pour les ressources
     */
    private class ResourceTableModel extends AbstractTableModel {
        
        private final String[] columnNames = {
            "Nom", "Type", "Description", "Statut"
        };
        
        @Override
        public int getRowCount() {
            return filteredResourcesList.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= filteredResourcesList.size()) {
                return null;
            }
            
            Resource resource = filteredResourcesList.get(rowIndex);
            
            return switch (columnIndex) {
                case 0 -> resource.getNom();
                case 1 -> resource.getType();
                case 2 -> resource.getDescription() != null ? resource.getDescription() : "";
                case 3 -> resource.isDisponible();
                default -> "";
            };
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 1 -> Resource.ResourceType.class;
                case 3 -> Boolean.class;
                default -> String.class;
            };
        }
    }
    
    /**
     * Renderer pour les types de ressource
     */
    private static class TypeCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Resource.ResourceType) {
                Resource.ResourceType type = (Resource.ResourceType) value;
                setText(type.getDisplayName());
                
                if (!isSelected) {
                    // Utiliser ColorUtils pour une couleur d'arrière-plan subtile
                    Color backgroundColor = ColorUtils.createLightTransparentColor(type.getColor());
                    Color textColor = Color.decode(type.getColor());
                    
                    setBackground(backgroundColor);
                    setForeground(textColor);
                } else {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                
                // Bordure avec la couleur du type
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode(type.getColor()), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
            
            return this;
        }
    }
    
    /**
     * Renderer pour le statut de disponibilité
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Boolean) {
                Boolean disponible = (Boolean) value;
                
                if (disponible) {
                    setText("🟢 Disponible");
                    if (!isSelected) {
                        Color backgroundColor = ColorUtils.createLightTransparentColor("#10B981");
                        setBackground(backgroundColor);
                        setForeground(Color.decode("#10B981"));
                    }
                } else {
                    setText("🔴 Indisponible");
                    if (!isSelected) {
                        Color backgroundColor = ColorUtils.createLightTransparentColor("#EF4444");
                        setBackground(backgroundColor);
                        setForeground(Color.decode("#EF4444"));
                    }
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                
                // Bordure selon le statut
                Color borderColor = disponible ? Color.decode("#10B981") : Color.decode("#EF4444");
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
            
            return this;
        }
    }
    
    /**
     * Renderer pour la ComboBox des types
     */
    private static class TypeComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Resource.ResourceType) {
                Resource.ResourceType type = (Resource.ResourceType) value;
                setText(type.getDisplayName());
            } else if (value == null) {
                setText("Tous les types");
            }
            
            return this;
        }
    }
}