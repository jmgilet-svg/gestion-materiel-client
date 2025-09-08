package com.materiel.client.view.devis;

import com.materiel.client.model.Devis;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.DevisService;

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
 * Panel de liste des devis avec fonctionnalités CRUD
 */
public class DevisListPanel extends JPanel {
    
    private JTable devisTable;
    private DevisTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton transformButton;
    private JTextField searchField;
    
    private List<Devis> devisList;
    
    public DevisListPanel() {
        devisList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Table des devis au centre
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
        
        // Titre et recherche
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📋 Gestion des Devis");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterDevis());
        
        JLabel searchLabel = new JLabel("🔍 Rechercher:");
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createHorizontalStrut(30));
        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        
        // Boutons d'action
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        addButton = new JButton("+ Nouveau Devis");
        addButton.setBackground(Color.decode("#3B82F6"));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(130, 35));
        addButton.addActionListener(e -> createNewDevis());
        
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
        tableModel = new DevisTableModel();
        devisTable = new JTable(tableModel);
        
        // Configuration de la table
        setupTable();
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(devisTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        devisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        devisTable.setRowHeight(40);
        devisTable.setShowGrid(true);
        devisTable.setGridColor(Color.decode("#E5E7EB"));
        devisTable.getTableHeader().setBackground(Color.decode("#F9FAFB"));
        devisTable.getTableHeader().setFont(devisTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Configuration des colonnes
        devisTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Numéro
        devisTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Client
        devisTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date
        devisTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Validité
        devisTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Statut
        devisTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Montant HT
        devisTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Montant TTC
        
        // Renderer personnalisé pour le statut
        devisTable.getColumnModel().getColumn(4).setCellRenderer(new StatutCellRenderer());
        
        // Renderer pour les montants
        DefaultTableCellRenderer montantRenderer = new DefaultTableCellRenderer();
        montantRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        devisTable.getColumnModel().getColumn(5).setCellRenderer(montantRenderer);
        devisTable.getColumnModel().getColumn(6).setCellRenderer(montantRenderer);
        
        // Double-clic pour éditer
        devisTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedDevis();
                }
            }
        });
        
        // Mise à jour des boutons selon la sélection
        devisTable.getSelectionModel().addListSelectionListener(e -> {
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
        panel.setPreferredSize(new Dimension(150, 0));
        
        JLabel actionsLabel = new JLabel("Actions");
        actionsLabel.setFont(actionsLabel.getFont().deriveFont(Font.BOLD, 14f));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        editButton = createActionButton("✏️ Modifier", this::editSelectedDevis);
        transformButton = createActionButton("➡️ Transformer", this::transformToCommande);
        deleteButton = createActionButton("🗑️ Supprimer", this::deleteSelectedDevis);
        
        // Désactiver les boutons par défaut
        editButton.setEnabled(false);
        transformButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(transformButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(deleteButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createActionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(130, 35));
        button.setPreferredSize(new Dimension(130, 35));
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // TODO: Implémenter DevisService
                // DevisService devisService = ServiceFactory.getDevisService();
                // devisList = devisService.getAllDevis();
                
                // Données de démonstration
                devisList = createSampleDevis();
                
                tableModel.fireTableDataChanged();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des devis: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private List<Devis> createSampleDevis() {
        List<Devis> samples = new ArrayList<>();
        
        // TODO: Créer des exemples de devis
        // Pour l'instant, retourner une liste vide
        
        return samples;
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void filterDevis() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            loadData();
        } else {
            // TODO: Implémenter le filtrage
            tableModel.fireTableDataChanged();
        }
    }
    
    private void createNewDevis() {
        DevisEditDialog dialog = new DevisEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Devis newDevis = dialog.getDevis();
            // TODO: Sauvegarder le devis
            refreshData();
        }
    }
    
    private void editSelectedDevis() {
        int selectedRow = devisTable.getSelectedRow();
        if (selectedRow >= 0) {
            Devis devis = devisList.get(selectedRow);
            
            DevisEditDialog dialog = new DevisEditDialog((Frame) SwingUtilities.getWindowAncestor(this), devis);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Devis updatedDevis = dialog.getDevis();
                // TODO: Sauvegarder les modifications
                refreshData();
            }
        }
    }
    
    private void transformToCommande() {
        int selectedRow = devisTable.getSelectedRow();
        if (selectedRow >= 0) {
            Devis devis = devisList.get(selectedRow);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous transformer ce devis en bon de commande ?\n" +
                "Devis: " + devis.getNumero(),
                "Transformation en commande",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // TODO: Implémenter la transformation
                JOptionPane.showMessageDialog(this,
                    "Devis transformé en bon de commande avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        }
    }
    
    private void deleteSelectedDevis() {
        int selectedRow = devisTable.getSelectedRow();
        if (selectedRow >= 0) {
            Devis devis = devisList.get(selectedRow);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce devis ?\n" +
                "Devis: " + devis.getNumero() + "\n" +
                "Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // TODO: Supprimer le devis
                refreshData();
            }
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = devisTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        
        // Le bouton transformer n'est actif que si le devis est accepté
        if (hasSelection) {
            Devis selectedDevis = devisList.get(devisTable.getSelectedRow());
            transformButton.setEnabled(selectedDevis.getStatut() == Devis.StatutDevis.ACCEPTE);
        } else {
            transformButton.setEnabled(false);
        }
    }
    
    /**
     * Modèle de table pour les devis
     */
    private class DevisTableModel extends AbstractTableModel {
        
        private final String[] columnNames = {
            "Numéro", "Client", "Date création", "Date validité", "Statut", "Montant HT", "Montant TTC"
        };
        
        @Override
        public int getRowCount() {
            return devisList.size();
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
            if (rowIndex >= devisList.size()) {
                return null;
            }
            
            Devis devis = devisList.get(rowIndex);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            return switch (columnIndex) {
                case 0 -> devis.getNumero();
                case 1 -> devis.getClient() != null ? devis.getClient().getNom() : "";
                case 2 -> devis.getDateCreation() != null ? devis.getDateCreation().format(dateFormatter) : "";
                case 3 -> devis.getDateValidite() != null ? devis.getDateValidite().format(dateFormatter) : "";
                case 4 -> devis.getStatut();
                case 5 -> String.format("%.2f €", devis.getMontantHT());
                case 6 -> String.format("%.2f €", devis.getMontantTTC());
                default -> "";
            };
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 4 -> Devis.StatutDevis.class;
                default -> String.class;
            };
        }
    }
    
    /**
     * Renderer personnalisé pour les statuts
     */
    private static class StatutCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Devis.StatutDevis) {
                Devis.StatutDevis statut = (Devis.StatutDevis) value;
                setText(statut.getDisplayName());
                
                if (!isSelected) {
                    setBackground(Color.decode(statut.getColor() + "20")); // Couleur avec transparence
                    setForeground(Color.decode(statut.getColor()));
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
            }
            
            return this;
        }
    }
} 
