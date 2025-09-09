package com.materiel.client.service;

import com.materiel.client.model.BonLivraison;
import com.materiel.client.model.Commande;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface pour les services de gestion des bons de livraison
 */
public interface BonLivraisonService {
    
    List<BonLivraison> getAllBonsLivraison();
    BonLivraison getBonLivraisonById(Long id);
    BonLivraison getBonLivraisonByNumero(String numero);
    BonLivraison saveBonLivraison(BonLivraison bonLivraison);
    void deleteBonLivraison(Long id);
    
    List<BonLivraison> getBonsLivraisonByClient(Long clientId);
    List<BonLivraison> getBonsLivraisonByStatut(BonLivraison.StatutBonLivraison statut);
    List<BonLivraison> getBonsLivraisonByDateRange(LocalDate startDate, LocalDate endDate);
    List<BonLivraison> getBonsLivraisonEnRetard();
    List<BonLivraison> getBonsLivraisonByTransporteur(String chauffeur);
    
    BonLivraison creerDepuisCommande(Commande commande);
    void demarrerTransport(Long bonLivraisonId);
    void confirmerLivraison(Long bonLivraisonId, String personneReceptionnee, String commentaires);
    void marquerRetourne(Long bonLivraisonId, String raison);
    
    List<BonLivraison> searchBonsLivraison(String searchTerm);
}