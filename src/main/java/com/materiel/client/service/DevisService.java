package com.materiel.client.service;

import com.materiel.client.model.Devis;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface pour les services de gestion des devis
 */
public interface DevisService {
    
    List<Devis> getAllDevis();
    Devis getDevisById(Long id);
    Devis getDevisByNumero(String numero);
    Devis saveDevis(Devis devis);
    void deleteDevis(Long id);
    
    List<Devis> getDevisByClient(Long clientId);
    List<Devis> getDevisByStatut(Devis.StatutDevis statut);
    List<Devis> getDevisByDateRange(LocalDate startDate, LocalDate endDate);
    
    Devis reviserDevis(Long devisId);
    void transformerEnBonCommande(Long devisId);
    void marquerExpires();
    
    List<Devis> searchDevis(String searchTerm);
}