// ClientService.java
package com.materiel.client.service;

import com.materiel.client.model.Client;
import java.util.List;

/**
 * Interface pour les services de gestion des clients
 */
public interface ClientService {
    
    List<Client> getAllClients();
    Client getClientById(Long id);
    Client saveClient(Client client);
    void deleteClient(Long id);
    
    List<Client> searchClients(String searchTerm);
    boolean isClientUsed(Long clientId);
}