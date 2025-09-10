package com.materiel.client.view.livraisons;

import com.materiel.client.model.BonLivraison;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.BonLivraisonService;
import com.materiel.client.view.ui.ColorUtils;

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
 * Panel de liste des bons de livraison avec fonctionnalités CRUD
 */
public class BonLivraisonListPanel extends JPanel {
    
    private JTable bonLivraisonTable;
    private BonLivraisonTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton startTransportButton;
    private JButton confirmDeliveryButton;
    private JButton markReturnedButton;
    private JTextField searchField;
    private JComboBox<BonLivraison.StatutBonLivraison> statusFilter;
    private JCheckBox retardFilter;
    
    private List<BonLivraison> bonLivraisonList;
    private List<BonLivraison> filteredBonLivraisonList;
    
    public BonLivraisonListPanel() {
        bonLivraisonList = new ArrayList<>();
        filteredBonLivraisonList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Table des BL au centre
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
        
        JLabel titleLabel = new JLabel("🚚 Gestion des Bons de Livraison");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        // Recherche
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterBonsLivraison());
        
        // Filtre par statut
        statusFilter = new JComboBox<>();
        statusFilter.addItem(null); // "Tous"
        for (BonLivraison.StatutBonLivraison statut : BonLivraison.StatutBonLivraison.values()) {
            statusFilter.addItem(statut);
        }
        statusFilter.setRenderer(new StatutComboBoxRenderer());
        statusFilter.addActionListener(e -> filterBonsLivraison());
        
        // Filtre retards
        retardFilter = new JCheckBox("En retard");
        retardFilter.addActionListener(e -> filterBonsLivraison());
        
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
        
        addButton = new JButton("+ Nouveau BL");
        addButton.setBackground(Color.decode("#3B82F6"));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(130, 35));
        addButton.addActionListener(e -> createNewBonLivraison());
        
        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setPreferredSize(new Dimension(110, 35));
        refreshButton.addActionListener(e -> refreshData());
        
        JButton retardButton = new JButton("⚠️ Voir retards");
        retardButton.setBackground(Color.decode("#F97316"));
        retardButton.setForeground(Color.WHITE);
        retardButton.setPreferredSize(new Dimension(120, 35));
        retardButton.addActionListener(e -> showRetards());
        
        JButton trackingButton = new JButton("🗺️ Suivi transport");
        trackingButton.setBackground(Color.decode("#8B5CF6"));
        trackingButton.setForeground(Color.WHITE);
        trackingButton.setPreferredSize(new Dimension(140, 35));
        trackingButton.addActionListener(e -> showTracking());
        
        rightPanel.add(trackingButton);
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
        tableModel = new BonLivraisonTableModel();
        bonLivraisonTable = new JTable(tableModel);
        
        // Configuration de la table
        setupTable();
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(bonLivraisonTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        bonLivraisonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bonLivraisonTable.setRowHeight(50);
        bonLivraisonTable.setShowGrid(true);
        bonLivraisonTable.setGridColor(Color.decode("#E5E7EB"));
        bonLivraisonTable.getTableHeader().setBackground(Color.decode("#F9FAFB"));
        bonLivraisonTable.getTableHeader().setFont(bonLivraisonTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Configuration des colonnes
        bonLivraisonTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Numéro
        bonLivraisonTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Client
        bonLivraisonTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date livraison
        bonLivraisonTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Statut
        bonLivraisonTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Chauffeur
        bonLivraisonTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Véhicule
        bonLivraisonTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Poids
        bonLivraisonTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Retard
        
        // Renderers personnalisés
        bonLivraisonTable.getColumnModel().getColumn(3).setCellRenderer(new StatutCellRenderer());
        bonLivraisonTable.getColumnModel().getColumn(7).setCellRenderer(new RetardCellRenderer());
        
        // Renderer pour le poids
        DefaultTableCellRenderer poidsRenderer = new DefaultTableCellRenderer();
        poidsRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        bonLivraisonTable.getColumnModel().getColumn(6).setCellRenderer(poidsRenderer);
        
        // Double-clic pour éditer
        bonLivraisonTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedBonLivraison();
                }
            }
        });
        
        // Mise à jour des boutons selon la sélection
        bonLivraisonTable.getSelectionModel().addListSelectionListener(e -> {
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
        panel.setPreferredSize(new Dimension(200, 0));
        
        JLabel actionsLabel = new JLabel("Actions");
        actionsLabel.setFont(actionsLabel.getFont().deriveFont(Font.BOLD, 14f));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        editButton = createActionButton("✏️ Modifier", this::editSelectedBonLivraison);
        startTransportButton = createActionButton("🚛 Démarrer transport", this::startTransport);
        confirmDeliveryButton = createActionButton("✅ Confirmer livraison", this::confirmDelivery);
        markReturnedButton = createActionButton("↩️ Marquer retourné", this::markReturned);
        deleteButton = createActionButton("🗑️ Supprimer", this::deleteSelectedBonLivraison);
        
        // Désactiver les boutons par défaut
        editButton.setEnabled(false);
        startTransportButton.setEnabled(false);
        confirmDeliveryButton.setEnabled(false);
        markReturnedButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(startTransportButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(confirmDeliveryButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(markReturnedButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(deleteButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createActionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 35));
        button.setPreferredSize(new Dimension(180, 35));
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                bonLivraisonList = bonLivraisonService.getAllBonsLivraison();
                
                filterBonsLivraison();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des bons de livraison: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void filterBonsLivraison() {
        String searchText = searchField.getText().toLowerCase().trim();
        BonLivraison.StatutBonLivraison selectedStatus = (BonLivraison.StatutBonLivraison) statusFilter.getSelectedItem();
        boolean showOnlyRetard = retardFilter.isSelected();
        
        filteredBonLivraisonList = bonLivraisonList.stream()
                .filter(bl -> {
                    // Filtre par texte
                    if (!searchText.isEmpty()) {
                        boolean matchesSearch = 
                            (bl.getNumero() != null && bl.getNumero().toLowerCase().contains(searchText)) ||
                            (bl.getClient() != null && bl.getClient().getNom() != null && 
                             bl.getClient().getNom().toLowerCase().contains(searchText)) ||
                            (bl.getChauffeur() != null && bl.getChauffeur().toLowerCase().contains(searchText)) ||
                            (bl.getNumeroImmatriculation() != null && 
                             bl.getNumeroImmatriculation().toLowerCase().contains(searchText));
                        if (!matchesSearch) return false;
                    }
                    
                    // Filtre par statut
                    if (selectedStatus != null && bl.getStatut() != selectedStatus) {
                        return false;
                    }
                    
                    // Filtre retards
                    if (showOnlyRetard && !bl.isEnRetard()) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        tableModel.fireTableDataChanged();
        updateButtonStates();
    }
    
    private void createNewBonLivraison() {
        BonLivraisonEditDialog dialog = new BonLivraisonEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                BonLivraison newBonLivraison = dialog.getBonLivraison();
                BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                bonLivraisonService.saveBonLivraison(newBonLivraison);
                refreshData();
                
                JOptionPane.showMessageDialog(this,
                    "Bon de livraison créé avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création du bon de livraison: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedBonLivraison() {
        int selectedRow = bonLivraisonTable.getSelectedRow();
        if (selectedRow >= 0) {
            BonLivraison bonLivraison = filteredBonLivraisonList.get(selectedRow);
            
            if (!bonLivraison.peutEtreModifie()) {
                JOptionPane.showMessageDialog(this,
                    "Ce bon de livraison ne peut plus être modifié (statut: " + bonLivraison.getStatut().getDisplayName() + ")",
                    "Modification impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BonLivraisonEditDialog dialog = new BonLivraisonEditDialog((Frame) SwingUtilities.getWindowAncestor(this), bonLivraison);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                try {
                    BonLivraison updatedBonLivraison = dialog.getBonLivraison();
                    BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                    bonLivraisonService.saveBonLivraison(updatedBonLivraison);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Bon de livraison modifié avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void startTransport() {
        int selectedRow = bonLivraisonTable.getSelectedRow();
        if (selectedRow >= 0) {
            BonLivraison bonLivraison = filteredBonLivraisonList.get(selectedRow);
            
            if (!bonLivraison.peutDemarrerTransport()) {
                JOptionPane.showMessageDialog(this,
                    "Le transport ne peut pas être démarré. Vérifiez que le chauffeur et le véhicule sont renseignés.",
                    "Démarrage impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                "Démarrer le transport pour le BL " + bonLivraison.getNumero() + " ?\n" +
                "Chauffeur: " + bonLivraison.getChauffeur() + "\n" +
                "Véhicule: " + bonLivraison.getVehicule(),
                "Confirmation de départ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                    bonLivraisonService.demarrerTransport(bonLivraison.getId());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Transport démarré avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du démarrage du transport: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void confirmDelivery() {
        int selectedRow = bonLivraisonTable.getSelectedRow();
        if (selectedRow >= 0) {
            BonLivraison bonLivraison = filteredBonLivraisonList.get(selectedRow);
            
            if (!bonLivraison.peutConfirmerLivraison()) {
                JOptionPane.showMessageDialog(this,
                    "La livraison ne peut pas être confirmée (statut: " + bonLivraison.getStatut().getDisplayName() + ")",
                    "Confirmation impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Dialogue pour saisir les détails de livraison
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
            panel.add(new JLabel("Personne réceptionnée:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            JTextField personneField = new JTextField(20);
            panel.add(personneField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Commentaires:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            JTextArea commentairesArea = new JTextArea(3, 20);
            commentairesArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(commentairesArea);
            panel.add(scrollPane, gbc);
            
            int result = JOptionPane.showConfirmDialog(this, panel,
                "Confirmer la livraison - " + bonLivraison.getNumero(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String personne = personneField.getText().trim();
                String commentaires = commentairesArea.getText().trim();
                
                if (personne.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "La personne réceptionnée est obligatoire.",
                        "Information manquante", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                    bonLivraisonService.confirmerLivraison(bonLivraison.getId(), personne, commentaires);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Livraison confirmée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la confirmation: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void markReturned() {
        int selectedRow = bonLivraisonTable.getSelectedRow();
        if (selectedRow >= 0) {
            BonLivraison bonLivraison = filteredBonLivraisonList.get(selectedRow);
            
            String raison = JOptionPane.showInputDialog(this,
                "Raison du retour pour le BL " + bonLivraison.getNumero() + ":",
                "Marquer comme retourné",
                JOptionPane.QUESTION_MESSAGE);
            
            if (raison != null && !raison.trim().isEmpty()) {
                try {
                    BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                    bonLivraisonService.marquerRetourne(bonLivraison.getId(), raison);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Bon de livraison marqué comme retourné.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du marquage: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSelectedBonLivraison() {
        int selectedRow = bonLivraisonTable.getSelectedRow();
        if (selectedRow >= 0) {
            BonLivraison bonLivraison = filteredBonLivraisonList.get(selectedRow);
            
            if (!bonLivraison.peutEtreModifie()) {
                JOptionPane.showMessageDialog(this,
                    "Ce bon de livraison ne peut pas être supprimé (statut: " + bonLivraison.getStatut().getDisplayName() + ")",
                    "Suppression impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce bon de livraison ?\n" +
                "BL: " + bonLivraison.getNumero() + "\n" +
                "Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
                    bonLivraisonService.deleteBonLivraison(bonLivraison.getId());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Bon de livraison supprimé avec succès !",
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
            BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
            List<BonLivraison> bonsLivraisonEnRetard = bonLivraisonService.getBonsLivraisonEnRetard();
            
            if (bonsLivraisonEnRetard.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun bon de livraison en retard !",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder();
                message.append("Bons de livraison en retard (").append(bonsLivraisonEnRetard.size()).append(") :\n\n");
                
                for (BonLivraison bl : bonsLivraisonEnRetard) {
                    message.append("• ").append(bl.getNumero())
                           .append(" - ").append(bl.getClient().getNom())
                           .append(" (prévu le ").append(bl.getDateLivraison().toLocalDate())
                           .append(")\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Livraisons en retard",
                    JOptionPane.WARNING_MESSAGE);
                
                // Activer le filtre retards
                retardFilter.setSelected(true);
                filterBonsLivraison();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des retards: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTracking() {
        try {
            BonLivraisonService bonLivraisonService = ServiceFactory.getBonLivraisonService();
            List<BonLivraison> bonsEnTransport = bonLivraisonService.getBonsLivraisonByStatut(BonLivraison.StatutBonLivraison.EN_TRANSPORT);
            
            if (bonsEnTransport.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun transport en cours !",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder();
                message.append("Transports en cours (").append(bonsEnTransport.size()).append(") :\n\n");
                
                for (BonLivraison bl : bonsEnTransport) {
                    message.append("🚛 ").append(bl.getNumero())
                           .append(" - ").append(bl.getChauffeur())
                           .append(" vers ").append(bl.getClient().getNom());
                    
                    if (bl.getHeureDepart() != null) {
                        long duree = java.time.Duration.between(bl.getHeureDepart(), java.time.LocalDateTime.now()).toHours();
                        message.append(" (parti il y a ").append(duree).append("h)");
                    }
                    message.append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Suivi des transports",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du suivi: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = bonLivraisonTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        startTransportButton.setEnabled(hasSelection);
        confirmDeliveryButton.setEnabled(hasSelection);
        markReturnedButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            BonLivraison selectedBL = filteredBonLivraisonList.get(bonLivraisonTable.getSelectedRow());
            editButton.setEnabled(selectedBL.peutEtreModifie());
            startTransportButton.setEnabled(selectedBL.peutDemarrerTransport());
            confirmDeliveryButton.setEnabled(selectedBL.peutConfirmerLivraison());
            deleteButton.setEnabled(selectedBL.peutEtreModifie());
        }
    }
    
    /**
     * Modèle de table pour les bons de livraison
     */
    private class BonLivraisonTableModel extends AbstractTableModel {
        
        private final String[] columnNames = {
            "Numéro", "Client", "Date livraison", "Statut", 
            "Chauffeur", "Véhicule", "Poids (T)", "Retard"
        };
        
        @Override
        public int getRowCount() {
            return filteredBonLivraisonList.size();
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
            if (rowIndex >= filteredBonLivraisonList.size()) {
                return null;
            }
            
            BonLivraison bonLivraison = filteredBonLivraisonList.get(rowIndex);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            return switch (columnIndex) {
                case 0 -> bonLivraison.getNumero();
                case 1 -> bonLivraison.getClient() != null ? bonLivraison.getClient().getNom() : "";
                case 2 -> bonLivraison.getDateLivraison() != null ? bonLivraison.getDateLivraison().format(dateFormatter) : "";
                case 3 -> bonLivraison.getStatut();
                case 4 -> bonLivraison.getChauffeur() != null ? bonLivraison.getChauffeur() : "";
                case 5 -> bonLivraison.getVehicule() != null ? bonLivraison.getVehicule() : "";
                case 6 -> bonLivraison.getPoidsTotal() != null ? bonLivraison.getPoidsTotal().toString() + "T" : "";
                case 7 -> bonLivraison.isEnRetard();
                default -> "";
            };
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 3 -> BonLivraison.StatutBonLivraison.class;
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
            
            if (value instanceof BonLivraison.StatutBonLivraison) {
                BonLivraison.StatutBonLivraison statut = (BonLivraison.StatutBonLivraison) value;
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
            
            if (value instanceof BonLivraison.StatutBonLivraison) {
                BonLivraison.StatutBonLivraison statut = (BonLivraison.StatutBonLivraison) value;
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