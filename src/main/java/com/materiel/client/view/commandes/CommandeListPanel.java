package com.materiel.client.view.commandes;

import com.materiel.client.model.Commande;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.CommandeService;
import com.materiel.client.util.ColorUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel de liste des commandes avec fonctionnalités CRUD
 */
public class CommandeListPanel extends JPanel {
    
    private JTable commandeTable;
    private CommandeTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton changeStatusButton;
    private JButton markDeliveredButton;
    private JTextField searchField;
    private JComboBox<Commande.StatutCommande> statusFilter;
    private JCheckBox retardFilter;
    
    private List<Commande> commandesList;
    private List<Commande> filteredCommandesList;
    
    public CommandeListPanel() {
        commandesList = new ArrayList<>();
        filteredCommandesList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Table des commandes au centre
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
        
        JLabel titleLabel = new JLabel("📦 Gestion des Commandes");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        // Recherche
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterCommandes());
        
        // Filtre par statut
        statusFilter = new JComboBox<>();
        statusFilter.addItem(null); // "Tous"
        for (Commande.StatutCommande statut : Commande.StatutCommande.values()) {
            statusFilter.addItem(statut);
        }
        statusFilter.setRenderer(new StatutComboBoxRenderer());
        statusFilter.addActionListener(e -> filterCommandes());
        
        // Filtre retards
        retardFilter = new JCheckBox("Commandes en retard");
        retardFilter.addActionListener(e -> filterCommandes());
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(new JLabel("🔍 Rechercher:"));
        leftPanel.add(searchField);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(new JLabel("Statut:"));
        leftPanel.add(statusFilter);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(retardFilter);
        
        // Boutons d'action
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        addButton = new JButton("+ Nouvelle Commande");
        addButton.setBackground(Color.decode("#3B82F6"));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(160, 35));
        addButton.addActionListener(e -> createNewCommande());
        
        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setPreferredSize(new Dimension(110, 35));
        refreshButton.addActionListener(e -> refreshData());
        
        JButton retardButton = new JButton("⚠️ Voir retards");
        retardButton.setBackground(Color.decode("#F97316"));
        retardButton.setForeground(Color.WHITE);
        retardButton.setPreferredSize(new Dimension(120, 35));
        retardButton.addActionListener(e -> showRetards());
        
        rightPanel.add(retardButton);
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
        tableModel = new CommandeTableModel();
        commandeTable = new JTable(tableModel);
        
        // Configuration de la table
        setupTable();
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(commandeTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        commandeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commandeTable.setRowHeight(45);
        commandeTable.setShowGrid(true);
        commandeTable.setGridColor(Color.decode("#E5E7EB"));
        commandeTable.getTableHeader().setBackground(Color.decode("#F9FAFB"));
        commandeTable.getTableHeader().setFont(commandeTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Configuration des colonnes
        commandeTable.getColumnModel().getColumn(0).setPreferredWidth(130); // Numéro
        commandeTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Client
        commandeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date création
        commandeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date livraison
        commandeTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Statut
        commandeTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Montant TTC
        commandeTable.getColumnModel().getColumn(6).setPreferredWidth(130); // Responsable
        commandeTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Retard
        
        // Renderers personnalisés
        commandeTable.getColumnModel().getColumn(4).setCellRenderer(new StatutCellRenderer());
        commandeTable.getColumnModel().getColumn(7).setCellRenderer(new RetardCellRenderer());
        
        // Renderer pour les montants
        DefaultTableCellRenderer montantRenderer = new DefaultTableCellRenderer();
        montantRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        commandeTable.getColumnModel().getColumn(5).setCellRenderer(montantRenderer);
        
        // Double-clic pour éditer
        commandeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedCommande();
                }
            }
        });
        
        // Mise à jour des boutons selon la sélection
        commandeTable.getSelectionModel().addListSelectionListener(e -> {
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
        panel.setPreferredSize(new Dimension(180, 0));
        
        JLabel actionsLabel = new JLabel("Actions");
        actionsLabel.setFont(actionsLabel.getFont().deriveFont(Font.BOLD, 14f));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        editButton = createActionButton("✏️ Modifier", this::editSelectedCommande);
        changeStatusButton = createActionButton("🔄 Changer statut", this::changeStatus);
        markDeliveredButton = createActionButton("✅ Marquer livrée", this::markDelivered);
        deleteButton = createActionButton("🗑️ Supprimer", this::deleteSelectedCommande);
        
        // Désactiver les boutons par défaut
        editButton.setEnabled(false);
        changeStatusButton.setEnabled(false);
        markDeliveredButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(changeStatusButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(markDeliveredButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(deleteButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createActionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 35));
        button.setPreferredSize(new Dimension(160, 35));
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                CommandeService commandeService = ServiceFactory.getCommandeService();
                commandesList = commandeService.getAllCommandes();
                
                filterCommandes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des commandes: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void filterCommandes() {
        String searchText = searchField.getText().toLowerCase().trim();
        Commande.StatutCommande selectedStatus = (Commande.StatutCommande) statusFilter.getSelectedItem();
        boolean showOnlyRetard = retardFilter.isSelected();
        
        filteredCommandesList = commandesList.stream()
                .filter(c -> {
                    // Filtre par texte
                    if (!searchText.isEmpty()) {
                        boolean matchesSearch = 
                            (c.getNumero() != null && c.getNumero().toLowerCase().contains(searchText)) ||
                            (c.getClient() != null && c.getClient().getNom() != null && 
                             c.getClient().getNom().toLowerCase().contains(searchText)) ||
                            (c.getResponsablePreparation() != null && 
                             c.getResponsablePreparation().toLowerCase().contains(searchText));
                        if (!matchesSearch) return false;
                    }
                    
                    // Filtre par statut
                    if (selectedStatus != null && c.getStatut() != selectedStatus) {
                        return false;
                    }
                    
                    // Filtre retards
                    if (showOnlyRetard && !c.isEnRetard()) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        tableModel.fireTableDataChanged();
        updateButtonStates();
    }
    
    private void createNewCommande() {
        CommandeEditDialog dialog = new CommandeEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                Commande newCommande = dialog.getCommande();
                CommandeService commandeService = ServiceFactory.getCommandeService();
                commandeService.saveCommande(newCommande);
                refreshData();
                
                JOptionPane.showMessageDialog(this,
                    "Commande créée avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedCommande() {
        int selectedRow = commandeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Commande commande = filteredCommandesList.get(selectedRow);
            
            if (!commande.peutEtreModifiee()) {
                JOptionPane.showMessageDialog(this,
                    "Cette commande ne peut plus être modifiée (statut: " + commande.getStatut().getDisplayName() + ")",
                    "Modification impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            CommandeEditDialog dialog = new CommandeEditDialog((Frame) SwingUtilities.getWindowAncestor(this), commande);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                try {
                    Commande updatedCommande = dialog.getCommande();
                    CommandeService commandeService = ServiceFactory.getCommandeService();
                    commandeService.saveCommande(updatedCommande);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Commande modifiée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de la commande: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void changeStatus() {
        int selectedRow = commandeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Commande commande = filteredCommandesList.get(selectedRow);
            
            Commande.StatutCommande[] statuts = Commande.StatutCommande.values();
            Commande.StatutCommande newStatus = (Commande.StatutCommande) JOptionPane.showInputDialog(
                this,
                "Choisir le nouveau statut pour la commande " + commande.getNumero() + ":",
                "Changement de statut",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuts,
                commande.getStatut()
            );
            
            if (newStatus != null && newStatus != commande.getStatut()) {
                try {
                    CommandeService commandeService = ServiceFactory.getCommandeService();
                    commandeService.changerStatut(commande.getId(), newStatus);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Statut mis à jour avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du changement de statut: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void markDelivered() {
        int selectedRow = commandeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Commande commande = filteredCommandesList.get(selectedRow);
            
            if (commande.getStatut() == Commande.StatutCommande.LIVREE) {
                JOptionPane.showMessageDialog(this,
                    "Cette commande est déjà marquée comme livrée.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                "Marquer la commande " + commande.getNumero() + " comme livrée aujourd'hui ?",
                "Confirmation de livraison",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    CommandeService commandeService = ServiceFactory.getCommandeService();
                    commandeService.marquerLivree(commande.getId(), java.time.LocalDate.now());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Commande marquée comme livrée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du marquage de livraison: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSelectedCommande() {
        int selectedRow = commandeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Commande commande = filteredCommandesList.get(selectedRow);
            
            if (!commande.peutEtreAnnulee()) {
                JOptionPane.showMessageDialog(this,
                    "Cette commande ne peut pas être supprimée (statut: " + commande.getStatut().getDisplayName() + ")",
                    "Suppression impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cette commande ?\n" +
                "Commande: " + commande.getNumero() + "\n" +
                "Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    CommandeService commandeService = ServiceFactory.getCommandeService();
                    commandeService.deleteCommande(commande.getId());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Commande supprimée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showRetards() {
        try {
            CommandeService commandeService = ServiceFactory.getCommandeService();
            List<Commande> commandesEnRetard = commandeService.getCommandesEnRetard();
            
            if (commandesEnRetard.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucune commande en retard !",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder();
                message.append("Commandes en retard (").append(commandesEnRetard.size()).append(") :\n\n");
                
                for (Commande cmd : commandesEnRetard) {
                    message.append("• ").append(cmd.getNumero())
                           .append(" - ").append(cmd.getClient().getNom())
                           .append(" (prévu le ").append(cmd.getDateLivraisonPrevue())
                           .append(")\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Commandes en retard",
                    JOptionPane.WARNING_MESSAGE);
                
                // Activer le filtre retards
                retardFilter.setSelected(true);
                filterCommandes();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des retards: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = commandeTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        changeStatusButton.setEnabled(hasSelection);
        markDeliveredButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            Commande selectedCommande = filteredCommandesList.get(commandeTable.getSelectedRow());
            editButton.setEnabled(selectedCommande.peutEtreModifiee());
            markDeliveredButton.setEnabled(selectedCommande.getStatut() != Commande.StatutCommande.LIVREE);
            deleteButton.setEnabled(selectedCommande.peutEtreAnnulee());
        }
    }
    
    /**
     * Modèle de table pour les commandes
     */
    private class CommandeTableModel extends AbstractTableModel {
        
        private final String[] columnNames = {
            "Numéro", "Client", "Date création", "Livraison prévue", "Statut", 
            "Montant TTC", "Responsable", "Retard"
        };
        
        @Override
        public int getRowCount() {
            return filteredCommandesList.size();
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
            if (rowIndex >= filteredCommandesList.size()) {
                return null;
            }
            
            Commande commande = filteredCommandesList.get(rowIndex);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            return switch (columnIndex) {
                case 0 -> commande.getNumero();
                case 1 -> commande.getClient() != null ? commande.getClient().getNom() : "";
                case 2 -> commande.getDateCreation() != null ? commande.getDateCreation().format(dateFormatter) : "";
                case 3 -> commande.getDateLivraisonPrevue() != null ? commande.getDateLivraisonPrevue().format(dateFormatter) : "";
                case 4 -> commande.getStatut();
                case 5 -> String.format("%.2f €", commande.getMontantTTC());
                case 6 -> commande.getResponsablePreparation() != null ? commande.getResponsablePreparation() : "";
                case 7 -> commande.isEnRetard();
                default -> "";
            };
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 4 -> Commande.StatutCommande.class;
                case 7 -> Boolean.class;
                default -> String.class;
            };
        }
    }
    
    /**
     * Renderer pour les statuts
     */
    private static class StatutCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Commande.StatutCommande) {
                Commande.StatutCommande statut = (Commande.StatutCommande) value;
                setText(statut.getDisplayName());
                
                if (!isSelected) {
                    Color backgroundColor = ColorUtils.createLightTransparentColor(statut.getColor());
                    Color textColor = Color.decode(statut.getColor());
                    
                    setBackground(backgroundColor);
                    setForeground(textColor);
                } else {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode(statut.getColor()), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
            
            return this;
        }
    }
    
    /**
     * Renderer pour les retards
     */
    private static class RetardCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Boolean) {
                Boolean enRetard = (Boolean) value;
                
                if (enRetard) {
                    setText("⚠️ RETARD");
                    if (!isSelected) {
                        setBackground(ColorUtils.createLightTransparentColor("#EF4444"));
                        setForeground(Color.decode("#EF4444"));
                    }
                } else {
                    setText("✅ À temps");
                    if (!isSelected) {
                        setBackground(ColorUtils.createLightTransparentColor("#10B981"));
                        setForeground(Color.decode("#10B981"));
                    }
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
            }
            
            return this;
        }
    }
    
    /**
     * Renderer pour la ComboBox des statuts
     */
    private static class StatutComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Commande.StatutCommande) {
                Commande.StatutCommande statut = (Commande.StatutCommande) value;
                setText("● " + statut.getDisplayName());
                
                if (!isSelected) {
                    setForeground(Color.decode(statut.getColor()));
                }
            } else if (value == null) {
                setText("Tous les statuts");
            }
            
            return this;
        }
    }
}