package com.materiel.client.service.impl;

import com.materiel.client.model.BonLivraison;
import com.materiel.client.model.Commande;
import com.materiel.client.model.Client;
import com.materiel.client.service.BonLivraisonService;
import com.materiel.client.mock.MockDataManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Implémentation Mock du service BonLivraison
 */
public class MockBonLivraisonService implements BonLivraisonService {
    
    private final MockDataManager mockDataManager;
    private final List<BonLivraison> bonsLivraison;
    
    public MockBonLivraisonService() {
        this.mockDataManager = MockDataManager.getInstance();
        this.bonsLivraison = new ArrayList<>();
        initializeDefaultBonsLivraison();
    }
    
    private void initializeDefaultBonsLivraison() {
        if (bonsLivraison.isEmpty()) {
            List<Client> clients = mockDataManager.getClients();
            if (!clients.isEmpty()) {
                createSampleBonsLivraison(clients);
            }
        }
    }
    
    private void createSampleBonsLivraison(List<Client> clients) {
        // BL 1 - En transport
        BonLivraison bl1 = new BonLivraison();
        bl1.setId(1L);
        bl1.setNumero("BL-20241208-001");
        bl1.setClient(clients.get(0));
        bl1.setDateCreation(LocalDate.now().minusDays(1));
        bl1.setDateLivraison(LocalDateTime.now().plusHours(3));
        bl1.setHeureDepart(LocalDateTime.now().minusHours(2));
        bl1.setStatut(BonLivraison.StatutBonLivraison.EN_TRANSPORT);
        bl1.setAdresseLivraison("123 Avenue des Bâtisseurs, 75020 Paris");
        bl1.setChauffeur("Jean Dupont");
        bl1.setVehicule("Camion Mercedes Actros");
        bl1.setNumeroImmatriculation("AB-123-CD");
        bl1.setPoidsTotal(new BigDecimal("15.5"));
        bonsLivraison.add(bl1);
        
        // BL 2 - Préparé
        BonLivraison bl2 = new BonLivraison();
        bl2.setId(2L);
        bl2.setNumero("BL-20241208-002");
        bl2.setClient(clients.size() > 1 ? clients.get(1) : clients.get(0));
        bl2.setDateCreation(LocalDate.now());
        bl2.setDateLivraison(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));
        bl2.setStatut(BonLivraison.StatutBonLivraison.PREPARE);
        bl2.setAdresseLivraison("45 Rue de la Paix, 69003 Lyon");
        bl2.setChauffeur("Marc Martin");
        bl2.setVehicule("Camion Volvo FH");
        bl2.setNumeroImmatriculation("EF-456-GH");
        bl2.setPoidsTotal(new BigDecimal("22.8"));
        bonsLivraison.add(bl2);
        
        // BL 3 - Livré
        BonLivraison bl3 = new BonLivraison();
        bl3.setId(3L);
        bl3.setNumero("BL-20241207-001");
        bl3.setClient(clients.size() > 2 ? clients.get(2) : clients.get(0));
        bl3.setDateCreation(LocalDate.now().minusDays(2));
        bl3.setDateLivraison(LocalDateTime.now().minusDays(1).withHour(14).withMinute(0));
        bl3.setHeureDepart(LocalDateTime.now().minusDays(1).withHour(8).withMinute(30));
        bl3.setHeureArrivee(LocalDateTime.now().minusDays(1).withHour(13).withMinute(45));
        bl3.setStatut(BonLivraison.StatutBonLivraison.LIVRE);
        bl3.setAdresseLivraison("67 Boulevard du Rhône, 69002 Lyon");
        bl3.setChauffeur("Pierre Dubois");
        bl3.setVehicule("Camion Scania R450");
        bl3.setNumeroImmatriculation("IJ-789-KL");
        bl3.setPoidsTotal(new BigDecimal("18.2"));
        bl3.setPersonneReceptionnee("Marie Durand");
        bl3.setSignatureClient("Signature_MD_20241207");
        bl3.setCommentairesLivraison("Livraison conforme, client satisfait");
        bonsLivraison.add(bl3);
        
        // BL 4 - En retard
        BonLivraison bl4 = new BonLivraison();
        bl4.setId(4L);
        bl4.setNumero("BL-20241206-001");
        bl4.setClient(clients.size() > 3 ? clients.get(3) : clients.get(0));
        bl4.setDateCreation(LocalDate.now().minusDays(3));
        bl4.setDateLivraison(LocalDateTime.now().minusHours(6)); // En retard
        bl4.setHeureDepart(LocalDateTime.now().minusHours(8));
        bl4.setStatut(BonLivraison.StatutBonLivraison.EN_TRANSPORT);
        bl4.setAdresseLivraison("89 La Canebière, 13001 Marseille");
        bl4.setChauffeur("Sophie Laurent");
        bl4.setVehicule("Camion MAN TGX");
        bl4.setNumeroImmatriculation("MN-012-OP");
        bl4.setPoidsTotal(new BigDecimal("25.7"));
        bl4.setCommentairesLivraison("Retard dû aux embouteillages sur l'A7");
        bonsLivraison.add(bl4);
        
        // BL 5 - Retourné
        BonLivraison bl5 = new BonLivraison();
        bl5.setId(5L);
        bl5.setNumero("BL-20241205-001");
        bl5.setClient(clients.size() > 4 ? clients.get(4) : clients.get(0));
        bl5.setDateCreation(LocalDate.now().minusDays(4));
        bl5.setDateLivraison(LocalDateTime.now().minusDays(2).withHour(10).withMinute(0));
        bl5.setHeureDepart(LocalDateTime.now().minusDays(2).withHour(7).withMinute(0));
        bl5.setHeureArrivee(LocalDateTime.now().minusDays(2).withHour(11).withMinute(30));
        bl5.setStatut(BonLivraison.StatutBonLivraison.RETOURNE);
        bl5.setAdresseLivraison("12 Place de la République, 59000 Lille");
        bl5.setChauffeur("Michel Bernard");
        bl5.setVehicule("Camion Renault T High");
        bl5.setNumeroImmatriculation("QR-345-ST");
        bl5.setPoidsTotal(new BigDecimal("12.4"));
        bl5.setRaisonRetour("Client absent, refus de livraison par le gardien");
        bl5.setCommentairesLivraison("Tentative de livraison, client non joignable");
        bonsLivraison.add(bl5);
    }
    
    @Override
    public List<BonLivraison> getAllBonsLivraison() {
        return new ArrayList<>(bonsLivraison);
    }
    
    @Override
    public BonLivraison getBonLivraisonById(Long id) {
        return bonsLivraison.stream()
                .filter(bl -> bl.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public BonLivraison getBonLivraisonByNumero(String numero) {
        return bonsLivraison.stream()
                .filter(bl -> numero.equals(bl.getNumero()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public BonLivraison saveBonLivraison(BonLivraison bonLivraison) {
        if (bonLivraison.getId() == null) {
            // Nouveau bon de livraison
            Long maxId = bonsLivraison.stream()
                    .mapToLong(BonLivraison::getId)
                    .max()
                    .orElse(0L);
            bonLivraison.setId(maxId + 1);
            
            // Générer un numéro si pas présent
            if (bonLivraison.getNumero() == null || bonLivraison.getNumero().isEmpty()) {
                bonLivraison.setNumero(genererNumeroBonLivraison());
            }
            
            bonsLivraison.add(bonLivraison);
        } else {
            // Mise à jour
            for (int i = 0; i < bonsLivraison.size(); i++) {
                if (bonsLivraison.get(i).getId().equals(bonLivraison.getId())) {
                    bonsLivraison.set(i, bonLivraison);
                    break;
                }
            }
        }
        
        return bonLivraison;
    }
    
    @Override
    public void deleteBonLivraison(Long id) {
        bonsLivraison.removeIf(bl -> bl.getId().equals(id));
    }
    
    @Override
    public List<BonLivraison> getBonsLivraisonByClient(Long clientId) {
        return bonsLivraison.stream()
                .filter(bl -> bl.getClient() != null && bl.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BonLivraison> getBonsLivraisonByStatut(BonLivraison.StatutBonLivraison statut) {
        return bonsLivraison.stream()
                .filter(bl -> bl.getStatut() == statut)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BonLivraison> getBonsLivraisonByDateRange(LocalDate startDate, LocalDate endDate) {
        return bonsLivraison.stream()
                .filter(bl -> {
                    LocalDate dateCreation = bl.getDateCreation();
                    return dateCreation != null && 
                           !dateCreation.isBefore(startDate) && 
                           !dateCreation.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BonLivraison> getBonsLivraisonEnRetard() {
        return bonsLivraison.stream()
                .filter(BonLivraison::isEnRetard)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BonLivraison> getBonsLivraisonByTransporteur(String chauffeur) {
        return bonsLivraison.stream()
                .filter(bl -> bl.getChauffeur() != null && 
                             bl.getChauffeur().toLowerCase().contains(chauffeur.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public BonLivraison creerDepuisCommande(Commande commande) {
        BonLivraison bonLivraison = new BonLivraison(commande);
        bonLivraison.setNumero(genererNumeroBonLivraison());
        
        return saveBonLivraison(bonLivraison);
    }
    
    @Override
    public void demarrerTransport(Long bonLivraisonId) {
        BonLivraison bonLivraison = getBonLivraisonById(bonLivraisonId);
        if (bonLivraison != null && bonLivraison.peutDemarrerTransport()) {
            bonLivraison.setStatut(BonLivraison.StatutBonLivraison.EN_TRANSPORT);
            bonLivraison.setHeureDepart(LocalDateTime.now());
            saveBonLivraison(bonLivraison);
        }
    }
    
    @Override
    public void confirmerLivraison(Long bonLivraisonId, String personneReceptionnee, String commentaires) {
        BonLivraison bonLivraison = getBonLivraisonById(bonLivraisonId);
        if (bonLivraison != null && bonLivraison.peutConfirmerLivraison()) {
            bonLivraison.setStatut(BonLivraison.StatutBonLivraison.LIVRE);
            bonLivraison.setHeureArrivee(LocalDateTime.now());
            bonLivraison.setPersonneReceptionnee(personneReceptionnee);
            bonLivraison.setCommentairesLivraison(commentaires);
            bonLivraison.setSignatureClient("Signature_" + personneReceptionnee + "_" + 
                                           LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            saveBonLivraison(bonLivraison);
        }
    }
    
    @Override
    public void marquerRetourne(Long bonLivraisonId, String raison) {
        BonLivraison bonLivraison = getBonLivraisonById(bonLivraisonId);
        if (bonLivraison != null) {
            bonLivraison.setStatut(BonLivraison.StatutBonLivraison.RETOURNE);
            bonLivraison.setRaisonRetour(raison);
            if (bonLivraison.getHeureArrivee() == null) {
                bonLivraison.setHeureArrivee(LocalDateTime.now());
            }
            saveBonLivraison(bonLivraison);
        }
    }
    
    @Override
    public List<BonLivraison> searchBonsLivraison(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBonsLivraison();
        }
        
        String term = searchTerm.toLowerCase().trim();
        return bonsLivraison.stream()
                .filter(bl -> 
                    (bl.getNumero() != null && bl.getNumero().toLowerCase().contains(term)) ||
                    (bl.getClient() != null && bl.getClient().getNom() != null && 
                     bl.getClient().getNom().toLowerCase().contains(term)) ||
                    (bl.getChauffeur() != null && bl.getChauffeur().toLowerCase().contains(term)) ||
                    (bl.getNumeroImmatriculation() != null && 
                     bl.getNumeroImmatriculation().toLowerCase().contains(term))
                )
                .collect(Collectors.toList());
    }
    
    private String genererNumeroBonLivraison() {
        LocalDate aujourd = LocalDate.now();
        String dateStr = aujourd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = bonsLivraison.stream()
                .filter(bl -> bl.getNumero() != null && bl.getNumero().startsWith("BL-" + dateStr))
                .count() + 1;
        return String.format("BL-%s-%03d", dateStr, count);
    }
}