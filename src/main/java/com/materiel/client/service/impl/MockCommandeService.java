package com.materiel.client.service.impl;

import com.materiel.client.model.Commande;
import com.materiel.client.model.Devis;
import com.materiel.client.model.Client;
import com.materiel.client.service.CommandeService;
import com.materiel.client.mock.MockDataManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Implémentation Mock du service Commande
 */
public class MockCommandeService implements CommandeService {
    
    private final MockDataManager mockDataManager;
    private final List<Commande> commandes;
    
    public MockCommandeService() {
        this.mockDataManager = MockDataManager.getInstance();
        this.commandes = new ArrayList<>();
        initializeDefaultCommandes();
    }
    
    private void initializeDefaultCommandes() {
        if (commandes.isEmpty()) {
            // Créer des commandes d'exemple
            List<Client> clients = mockDataManager.getClients();
            if (!clients.isEmpty()) {
                createSampleCommandes(clients);
            }
        }
    }
    
    private void createSampleCommandes(List<Client> clients) {
        // Commande 1 - Confirmée
        Commande cmd1 = new Commande();
        cmd1.setId(1L);
        cmd1.setNumero("CMD-20241208-001");
        cmd1.setClient(clients.get(0));
        cmd1.setDateCreation(LocalDate.now().minusDays(3));
        cmd1.setDateLivraisonPrevue(LocalDate.now().plusDays(4));
        cmd1.setStatut(Commande.StatutCommande.CONFIRMEE);
        cmd1.setAdresseLivraison("123 Avenue des Bâtisseurs, 75020 Paris");
        cmd1.setMontantHT(new BigDecimal("2500.00"));
        cmd1.setMontantTVA(new BigDecimal("500.00"));
        cmd1.setMontantTTC(new BigDecimal("3000.00"));
        cmd1.setResponsablePreparation("Pierre Martin");
        commandes.add(cmd1);
        
        // Commande 2 - En préparation
        Commande cmd2 = new Commande();
        cmd2.setId(2L);
        cmd2.setNumero("CMD-20241207-001");
        cmd2.setClient(clients.size() > 1 ? clients.get(1) : clients.get(0));
        cmd2.setDateCreation(LocalDate.now().minusDays(5));
        cmd2.setDateLivraisonPrevue(LocalDate.now().plusDays(2));
        cmd2.setStatut(Commande.StatutCommande.EN_PREPARATION);
        cmd2.setAdresseLivraison("45 Rue de la Paix, 69003 Lyon");
        cmd2.setMontantHT(new BigDecimal("4200.00"));
        cmd2.setMontantTVA(new BigDecimal("840.00"));
        cmd2.setMontantTTC(new BigDecimal("5040.00"));
        cmd2.setResponsablePreparation("Marie Dubois");
        commandes.add(cmd2);
        
        // Commande 3 - Prête
        Commande cmd3 = new Commande();
        cmd3.setId(3L);
        cmd3.setNumero("CMD-20241206-001");
        cmd3.setClient(clients.size() > 2 ? clients.get(2) : clients.get(0));
        cmd3.setDateCreation(LocalDate.now().minusDays(7));
        cmd3.setDateLivraisonPrevue(LocalDate.now().plusDays(1));
        cmd3.setStatut(Commande.StatutCommande.PRETE);
        cmd3.setAdresseLivraison("67 Boulevard du Rhône, 69002 Lyon");
        cmd3.setMontantHT(new BigDecimal("1800.00"));
        cmd3.setMontantTVA(new BigDecimal("360.00"));
        cmd3.setMontantTTC(new BigDecimal("2160.00"));
        cmd3.setResponsablePreparation("Jean Dupont");
        commandes.add(cmd3);
        
        // Commande 4 - En retard
        Commande cmd4 = new Commande();
        cmd4.setId(4L);
        cmd4.setNumero("CMD-20241201-001");
        cmd4.setClient(clients.size() > 3 ? clients.get(3) : clients.get(0));
        cmd4.setDateCreation(LocalDate.now().minusDays(10));
        cmd4.setDateLivraisonPrevue(LocalDate.now().minusDays(2)); // En retard
        cmd4.setStatut(Commande.StatutCommande.EN_PREPARATION);
        cmd4.setAdresseLivraison("89 La Canebière, 13001 Marseille");
        cmd4.setMontantHT(new BigDecimal("3500.00"));
        cmd4.setMontantTVA(new BigDecimal("700.00"));
        cmd4.setMontantTTC(new BigDecimal("4200.00"));
        cmd4.setResponsablePreparation("Sophie Laurent");
        cmd4.setCommentaires("Retard dû à un problème d'approvisionnement");
        commandes.add(cmd4);
        
        // Commande 5 - Livrée
        Commande cmd5 = new Commande();
        cmd5.setId(5L);
        cmd5.setNumero("CMD-20241125-001");
        cmd5.setClient(clients.size() > 4 ? clients.get(4) : clients.get(0));
        cmd5.setDateCreation(LocalDate.now().minusDays(15));
        cmd5.setDateLivraisonPrevue(LocalDate.now().minusDays(5));
        cmd5.setDateLivraisonEffective(LocalDate.now().minusDays(3));
        cmd5.setStatut(Commande.StatutCommande.LIVREE);
        cmd5.setAdresseLivraison("12 Place de la République, 59000 Lille");
        cmd5.setMontantHT(new BigDecimal("2800.00"));
        cmd5.setMontantTVA(new BigDecimal("560.00"));
        cmd5.setMontantTTC(new BigDecimal("3360.00"));
        cmd5.setResponsablePreparation("Michel Bernard");
        commandes.add(cmd5);
    }
    
    @Override
    public List<Commande> getAllCommandes() {
        return new ArrayList<>(commandes);
    }
    
    @Override
    public Commande getCommandeById(Long id) {
        return commandes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Commande getCommandeByNumero(String numero) {
        return commandes.stream()
                .filter(c -> numero.equals(c.getNumero()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Commande saveCommande(Commande commande) {
        if (commande.getId() == null) {
            // Nouvelle commande
            Long maxId = commandes.stream()
                    .mapToLong(Commande::getId)
                    .max()
                    .orElse(0L);
            commande.setId(maxId + 1);
            
            // Générer un numéro si pas présent
            if (commande.getNumero() == null || commande.getNumero().isEmpty()) {
                commande.setNumero(genererNumeroCommande());
            }
            
            commandes.add(commande);
        } else {
            // Mise à jour
            for (int i = 0; i < commandes.size(); i++) {
                if (commandes.get(i).getId().equals(commande.getId())) {
                    commandes.set(i, commande);
                    break;
                }
            }
        }
        
        return commande;
    }
    
    @Override
    public void deleteCommande(Long id) {
        commandes.removeIf(c -> c.getId().equals(id));
    }
    
    @Override
    public List<Commande> getCommandesByClient(Long clientId) {
        return commandes.stream()
                .filter(c -> c.getClient() != null && c.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Commande> getCommandesByStatut(Commande.StatutCommande statut) {
        return commandes.stream()
                .filter(c -> c.getStatut() == statut)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Commande> getCommandesByDateRange(LocalDate startDate, LocalDate endDate) {
        return commandes.stream()
                .filter(c -> {
                    LocalDate dateCreation = c.getDateCreation();
                    return dateCreation != null && 
                           !dateCreation.isBefore(startDate) && 
                           !dateCreation.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Commande> getCommandesEnRetard() {
        return commandes.stream()
                .filter(Commande::isEnRetard)
                .collect(Collectors.toList());
    }
    
    @Override
    public Commande creerDepuisDevis(Devis devis) {
        Commande commande = new Commande(devis);
        commande.setNumero(genererNumeroCommande());
        commande.setAdresseLivraison(devis.getClient().getAdresse());
        commande.setDateLivraisonPrevue(LocalDate.now().plusDays(7)); // 1 semaine par défaut
        
        return saveCommande(commande);
    }
    
    @Override
    public void changerStatut(Long commandeId, Commande.StatutCommande nouveauStatut) {
        Commande commande = getCommandeById(commandeId);
        if (commande != null) {
            commande.setStatut(nouveauStatut);
            saveCommande(commande);
        }
    }
    
    @Override
    public void marquerLivree(Long commandeId, LocalDate dateLivraison) {
        Commande commande = getCommandeById(commandeId);
        if (commande != null) {
            commande.setStatut(Commande.StatutCommande.LIVREE);
            commande.setDateLivraisonEffective(dateLivraison);
            saveCommande(commande);
        }
    }
    
    @Override
    public List<Commande> searchCommandes(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCommandes();
        }
        
        String term = searchTerm.toLowerCase().trim();
        return commandes.stream()
                .filter(c -> 
                    (c.getNumero() != null && c.getNumero().toLowerCase().contains(term)) ||
                    (c.getClient() != null && c.getClient().getNom() != null && 
                     c.getClient().getNom().toLowerCase().contains(term)) ||
                    (c.getResponsablePreparation() != null && 
                     c.getResponsablePreparation().toLowerCase().contains(term))
                )
                .collect(Collectors.toList());
    }
    
    private String genererNumeroCommande() {
        LocalDate aujourd = LocalDate.now();
        String dateStr = aujourd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = commandes.stream()
                .filter(c -> c.getNumero() != null && c.getNumero().startsWith("CMD-" + dateStr))
                .count() + 1;
        return String.format("CMD-%s-%03d", dateStr, count);
    }
}