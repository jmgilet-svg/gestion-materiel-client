package com.materiel.client.view.clients;

import com.materiel.client.model.Client;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.service.ClientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel de liste des clients avec fonctionnalités CRUD
 */
public class ClientListPanel extends JPanel {
    
    private JTable clientTable;
    private ClientTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField searchField;
    
    private List<Client> clientsList;
    
    public ClientListPanel() {
        clientsList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        
        // Toolbar en haut
        JPanel toolbarPanel = createToolbarPanel();
        add(toolbarPanel, BorderLayout.NORTH);
        
        // Table des clients au centre
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
        
        JLabel titleLabel = new JLabel("👥 Gestion des Clients");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> filterClients());
        
        JLabel searchLabel = new JLabel("🔍 Rechercher:");
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createHorizontalStrut(30));
        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        
        // Boutons d'action
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        addButton = new JButton("+ Nouveau Client");
        addButton.setBackground(Color.decode("#10B981"));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(140, 35));
        addButton.addActionListener(e -> createNewClient());
        
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
        tableModel = new ClientTableModel();
        clientTable = new JTable(tableModel);
        
        // Configuration de la table
        setupTable();
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setRowHeight(35);
        clientTable.setShowGrid(true);
        clientTable.setGridColor(Color.decode("#E5E7EB"));
        clientTable.getTableHeader().setBackground(Color.decode("#F9FAFB"));
        clientTable.getTableHeader().setFont(clientTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Configuration des colonnes
        clientTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Nom
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Adresse
        clientTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Téléphone
        clientTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Email
        clientTable.getColumnModel().getColumn(4).setPreferredWidth(120); // SIRET
        
        // Double-clic pour éditer
        clientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedClient();
                }
            }
        });
        
        // Mise à jour des boutons selon la sélection
        clientTable.getSelectionModel().addListSelectionListener(e -> {
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
        
        editButton = createActionButton("✏️ Modifier", this::editSelectedClient);
        deleteButton = createActionButton("🗑️ Supprimer", this::deleteSelectedClient);
        
        // Désactiver les boutons par défaut
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(editButton);
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
                ClientService clientService = ServiceFactory.getClientService();
                clientsList = clientService.getAllClients();
                
                tableModel.fireTableDataChanged();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des clients: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void filterClients() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            loadData();
        } else {
            try {
                ClientService clientService = ServiceFactory.getClientService();
                clientsList = clientService.searchClients(searchText);
                tableModel.fireTableDataChanged();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la recherche: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void createNewClient() {
        ClientEditDialog dialog = new ClientEditDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                Client newClient = dialog.getClient();
                ClientService clientService = ServiceFactory.getClientService();
                clientService.saveClient(newClient);
                refreshData();
                
                JOptionPane.showMessageDialog(this,
                    "Client créé avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0) {
            Client client = clientsList.get(selectedRow);
            
            ClientEditDialog dialog = new ClientEditDialog((Frame) SwingUtilities.getWindowAncestor(this), client);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                try {
                    Client updatedClient = dialog.getClient();
                    ClientService clientService = ServiceFactory.getClientService();
                    clientService.saveClient(updatedClient);
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Client modifié avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification du client: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void deleteSelectedClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0) {
            Client client = clientsList.get(selectedRow);
            
            // Vérifier si le client est utilisé
            try {
                ClientService clientService = ServiceFactory.getClientService();
                if (clientService.isClientUsed(client.getId())) {
                    JOptionPane.showMessageDialog(this,
                        "Ce client ne peut pas être supprimé car il est utilisé dans des devis ou interventions.",
                        "Suppression impossible", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                // En cas d'erreur, demander confirmation
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce client ?\n" +
                "Client: " + client.getNom() + "\n" +
                "Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    ClientService clientService = ServiceFactory.getClientService();
                    clientService.deleteClient(client.getId());
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this,
                        "Client supprimé avec succès !",
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
        boolean hasSelection = clientTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * Modèle de table pour les clients
     */
    private class ClientTableModel extends AbstractTableModel {
        
        private final String[] columnNames = {
            "Nom", "Adresse", "Téléphone", "Email", "SIRET"
        };
        
        @Override
        public int getRowCount() {
            return clientsList.size();
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
            if (rowIndex >= clientsList.size()) {
                return null;
            }
            
            Client client = clientsList.get(rowIndex);
            
            return switch (columnIndex) {
                case 0 -> client.getNom();
                case 1 -> client.getAdresse() != null ? client.getAdresse() : "";
                case 2 -> client.getTelephone() != null ? client.getTelephone() : "";
                case 3 -> client.getEmail() != null ? client.getEmail() : "";
                case 4 -> client.getSiret() != null ? client.getSiret() : "";
                default -> "";
            };
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
}