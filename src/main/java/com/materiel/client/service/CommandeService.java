package com.materiel.client.service;

import com.materiel.client.model.Commande;
import com.materiel.client.model.Devis;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface pour les services de gestion des commandes
 */
public interface CommandeService {
    
    List<Commande> getAllCommandes();
    Commande getCommandeById(Long id);
    Commande getCommandeByNumero(String numero);
    Commande saveCommande(Commande commande);
    void deleteCommande(Long id);
    
    List<Commande> getCommandesByClient(Long clientId);
    List<Commande> getCommandesByStatut(Commande.StatutCommande statut);
    List<Commande> getCommandesByDateRange(LocalDate startDate, LocalDate endDate);
    List<Commande> getCommandesEnRetard();
    
    Commande creerDepuisDevis(Devis devis);
    void changerStatut(Long commandeId, Commande.StatutCommande nouveauStatut);
    void marquerLivree(Long commandeId, LocalDate dateLivraison);
    
    List<Commande> searchCommandes(String searchTerm);
}