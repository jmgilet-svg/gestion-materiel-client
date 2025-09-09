package com.materiel.client.service.impl;

import com.materiel.client.model.Client;
import com.materiel.client.service.ClientService;
import com.materiel.client.mock.MockDataManager;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implémentation Mock du service Client
 */
public class MockClientService implements ClientService {
    
    private final MockDataManager mockDataManager;
    
    public MockClientService() {
        this.mockDataManager = MockDataManager.getInstance();
    }
    
    @Override
    public List<Client> getAllClients() {
        return mockDataManager.getClients();
    }
    
    @Override
    public Client getClientById(Long id) {
        return getAllClients().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Client saveClient(Client client) {
        List<Client> clients = new ArrayList<>(getAllClients());
        
        if (client.getId() == null) {
            // Nouveau client
            Long maxId = clients.stream()
                    .mapToLong(Client::getId)
                    .max()
                    .orElse(0L);
            client.setId(maxId + 1);
            clients.add(client);
        } else {
            // Mise à jour
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getId().equals(client.getId())) {
                    clients.set(i, client);
                    break;
                }
            }
        }
        
        mockDataManager.saveClients(clients);
        return client;
    }
    
    @Override
    public void deleteClient(Long id) {
        List<Client> clients = getAllClients().stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());
        mockDataManager.saveClients(clients);
    }
    
    @Override
    public List<Client> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllClients();
        }
        
        String term = searchTerm.toLowerCase().trim();
        return getAllClients().stream()
                .filter(c -> 
                    (c.getNom() != null && c.getNom().toLowerCase().contains(term)) ||
                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(term)) ||
                    (c.getSiret() != null && c.getSiret().contains(term))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isClientUsed(Long clientId) {
        // Vérifier si le client est utilisé dans des devis ou interventions
        // TODO: Implémenter la vérification dans les autres entités
        return false;
    }
}