package com.materiel.client.service.impl;

import com.materiel.client.model.Devis;
import com.materiel.client.model.Client;
import com.materiel.client.service.DevisService;
import com.materiel.client.mock.MockDataManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Implémentation Mock du service Devis
 */
public class MockDevisService implements DevisService {
    
    private final MockDataManager mockDataManager;
    
    public MockDevisService() {
        this.mockDataManager = MockDataManager.getInstance();
    }
    
    @Override
    public List<Devis> getAllDevis() {
        List<Devis> devisList = mockDataManager.getDevis();
        
        // Si la liste est vide, créer des données d'exemple
        if (devisList.isEmpty()) {
            devisList = createSampleDevis();
            mockDataManager.saveDevis(devisList);
        }
        
        return devisList;
    }
    
    @Override
    public Devis getDevisById(Long id) {
        return getAllDevis().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Devis getDevisByNumero(String numero) {
        return getAllDevis().stream()
                .filter(d -> numero.equals(d.getNumero()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Devis saveDevis(Devis devis) {
        List<Devis> devisList = new ArrayList<>(getAllDevis());
        
        if (devis.getId() == null) {
            // Nouveau devis
            Long maxId = devisList.stream()
                    .mapToLong(Devis::getId)
                    .max()
                    .orElse(0L);
            devis.setId(maxId + 1);
            
            // Générer un numéro si pas présent
            if (devis.getNumero() == null || devis.getNumero().isEmpty()) {
                devis.setNumero(genererNumeroDevis());
            }
            
            devisList.add(devis);
        } else {
            // Mise à jour
            for (int i = 0; i < devisList.size(); i++) {
                if (devisList.get(i).getId().equals(devis.getId())) {
                    devisList.set(i, devis);
                    break;
                }
            }
        }
        
        mockDataManager.saveDevis(devisList);
        return devis;
    }
    
    @Override
    public void deleteDevis(Long id) {
        List<Devis> devisList = getAllDevis().stream()
                .filter(d -> !d.getId().equals(id))
                .collect(Collectors.toList());
        mockDataManager.saveDevis(devisList);
    }
    
    @Override
    public List<Devis> getDevisByClient(Long clientId) {
        return getAllDevis().stream()
                .filter(d -> d.getClient() != null && d.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Devis> getDevisByStatut(Devis.StatutDevis statut) {
        return getAllDevis().stream()
                .filter(d -> d.getStatut() == statut)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Devis> getDevisByDateRange(LocalDate startDate, LocalDate endDate) {
        return getAllDevis().stream()
                .filter(d -> {
                    LocalDate dateCreation = d.getDateCreation();
                    return dateCreation != null && 
                           !dateCreation.isBefore(startDate) && 
                           !dateCreation.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Devis reviserDevis(Long devisId) {
        Devis devisOriginal = getDevisById(devisId);
        if (devisOriginal == null) {
            throw new RuntimeException("Devis non trouvé");
        }
        
        // Créer une nouvelle version
        Devis nouvelleVersion = new Devis();
        nouvelleVersion.setNumero(devisOriginal.getNumero() + "-V" + (devisOriginal.getVersion() + 1));
        nouvelleVersion.setClient(devisOriginal.getClient());
        nouvelleVersion.setVersion(devisOriginal.getVersion() + 1);
        nouvelleVersion.setDateValidite(LocalDate.now().plusDays(30));
        nouvelleVersion.setMontantHT(devisOriginal.getMontantHT());
        nouvelleVersion.setMontantTVA(devisOriginal.getMontantTVA());
        nouvelleVersion.setMontantTTC(devisOriginal.getMontantTTC());
        
        return saveDevis(nouvelleVersion);
    }
    
    @Override
    public void transformerEnBonCommande(Long devisId) {
        Devis devis = getDevisById(devisId);
        if (devis == null) {
            throw new RuntimeException("Devis non trouvé");
        }
        
        if (devis.getStatut() != Devis.StatutDevis.ACCEPTE) {
            throw new RuntimeException("Le devis doit être accepté pour être transformé en bon de commande");
        }
        
        // TODO: Créer le bon de commande
        // Pour l'instant, on change juste le statut
        devis.setStatut(Devis.StatutDevis.ACCEPTE); // Pourrait être un autre statut
        saveDevis(devis);
    }
    
    @Override
    public void marquerExpires() {
        List<Devis> devisExpires = getAllDevis().stream()
                .filter(d -> d.getDateValidite() != null && 
                           d.getDateValidite().isBefore(LocalDate.now()) && 
                           d.getStatut() == Devis.StatutDevis.ENVOYE)
                .collect(Collectors.toList());
        
        devisExpires.forEach(devis -> {
            devis.setStatut(Devis.StatutDevis.EXPIRE);
            saveDevis(devis);
        });
    }
    
    @Override
    public List<Devis> searchDevis(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllDevis();
        }
        
        String term = searchTerm.toLowerCase().trim();
        return getAllDevis().stream()
                .filter(d -> 
                    (d.getNumero() != null && d.getNumero().toLowerCase().contains(term)) ||
                    (d.getClient() != null && d.getClient().getNom() != null && 
                     d.getClient().getNom().toLowerCase().contains(term))
                )
                .collect(Collectors.toList());
    }
    
    private String genererNumeroDevis() {
        LocalDate aujourd = LocalDate.now();
        String dateStr = aujourd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = getAllDevis().stream()
                .filter(d -> d.getNumero() != null && d.getNumero().startsWith("DEV-" + dateStr))
                .count() + 1;
        return String.format("DEV-%s-%03d", dateStr, count);
    }
    
    private List<Devis> createSampleDevis() {
        List<Devis> samples = new ArrayList<>();
        List<Client> clients = mockDataManager.getClients();
        
        if (!clients.isEmpty()) {
            // Devis 1 - En cours
            Devis devis1 = new Devis();
            devis1.setId(1L);
            devis1.setNumero("DEV-20241208-001");
            devis1.setClient(clients.get(0));
            devis1.setDateCreation(LocalDate.now().minusDays(5));
            devis1.setDateValidite(LocalDate.now().plusDays(25));
            devis1.setStatut(Devis.StatutDevis.ENVOYE);
            devis1.setMontantHT(new BigDecimal("2500.00"));
            devis1.setMontantTVA(new BigDecimal("500.00"));
            devis1.setMontantTTC(new BigDecimal("3000.00"));
            samples.add(devis1);
            
            // Devis 2 - Accepté
            if (clients.size() > 1) {
                Devis devis2 = new Devis();
                devis2.setId(2L);
                devis2.setNumero("DEV-20241207-001");
                devis2.setClient(clients.get(1));
                devis2.setDateCreation(LocalDate.now().minusDays(3));
                devis2.setDateValidite(LocalDate.now().plusDays(27));
                devis2.setStatut(Devis.StatutDevis.ACCEPTE);
                devis2.setMontantHT(new BigDecimal("4200.00"));
                devis2.setMontantTVA(new BigDecimal("840.00"));
                devis2.setMontantTTC(new BigDecimal("5040.00"));
                samples.add(devis2);
            }
            
            // Devis 3 - Brouillon
            if (clients.size() > 2) {
                Devis devis3 = new Devis();
                devis3.setId(3L);
                devis3.setNumero("DEV-20241208-002");
                devis3.setClient(clients.get(2));
                devis3.setDateCreation(LocalDate.now().minusDays(1));
                devis3.setDateValidite(LocalDate.now().plusDays(29));
                devis3.setStatut(Devis.StatutDevis.BROUILLON);
                devis3.setMontantHT(new BigDecimal("1800.00"));
                devis3.setMontantTVA(new BigDecimal("360.00"));
                devis3.setMontantTTC(new BigDecimal("2160.00"));
                samples.add(devis3);
            }
        }
        
        return samples;
    }
}