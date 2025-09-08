package com.materiel.client.service.impl;

import com.materiel.client.model.Intervention;
import com.materiel.client.service.InterventionService;
import com.materiel.client.mock.MockDataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implémentation Mock du service Intervention
 */
public class MockInterventionService implements InterventionService {
    
    private final MockDataManager mockDataManager;
    
    public MockInterventionService() {
        this.mockDataManager = MockDataManager.getInstance();
    }
    
    @Override
    public List<Intervention> getAllInterventions() {
        return mockDataManager.getInterventions();
    }
    
    @Override
    public List<Intervention> getInterventionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getAllInterventions().stream()
                .filter(intervention -> {
                    if (intervention.getDateDebut() == null) return false;
                    LocalDate interventionDate = intervention.getDateDebut().toLocalDate();
                    return !interventionDate.isBefore(startDate) && !interventionDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Intervention getInterventionById(Long id) {
        return getAllInterventions().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Intervention saveIntervention(Intervention intervention) {
        List<Intervention> interventions = new ArrayList<>(getAllInterventions());
        
        if (intervention.getId() == null) {
            // Nouvelle intervention
            Long maxId = interventions.stream()
                    .mapToLong(Intervention::getId)
                    .max()
                    .orElse(0L);
            intervention.setId(maxId + 1);
            interventions.add(intervention);
        } else {
            // Mise à jour
            for (int i = 0; i < interventions.size(); i++) {
                if (interventions.get(i).getId().equals(intervention.getId())) {
                    interventions.set(i, intervention);
                    break;
                }
            }
        }
        
        mockDataManager.saveInterventions(interventions);
        return intervention;
    }
    
    @Override
    public void deleteIntervention(Long id) {
        List<Intervention> interventions = getAllInterventions().stream()
                .filter(i -> !i.getId().equals(id))
                .collect(Collectors.toList());
        mockDataManager.saveInterventions(interventions);
    }
    
    @Override
    public List<Intervention> getInterventionsByClient(Long clientId) {
        return getAllInterventions().stream()
                .filter(i -> i.getClient() != null && i.getClient().getId().equals(clientId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Intervention> getInterventionsByResource(Long resourceId) {
        return getAllInterventions().stream()
                .filter(i -> i.getRessources() != null && 
                           i.getRessources().stream().anyMatch(r -> r.getId().equals(resourceId)))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasConflict(Intervention intervention) {
        if (intervention.getRessources() == null || intervention.getRessources().isEmpty()) {
            return false;
        }
        
        // Vérifier les conflits pour chaque ressource
        List<Intervention> existingInterventions = getInterventionsByDateRange(
            intervention.getDateDebut().toLocalDate(),
            intervention.getDateFin().toLocalDate()
        );
        
        for (Intervention existing : existingInterventions) {
            // Ignorer l'intervention elle-même si c'est une mise à jour
            if (intervention.getId() != null && intervention.getId().equals(existing.getId())) {
                continue;
            }
            
            // Vérifier le chevauchement temporel
            if (hasTimeOverlap(intervention, existing)) {
                // Vérifier si les ressources se chevauchent
                if (hasResourceOverlap(intervention, existing)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean hasTimeOverlap(Intervention intervention1, Intervention intervention2) {
        return intervention1.getDateDebut().isBefore(intervention2.getDateFin()) &&
               intervention1.getDateFin().isAfter(intervention2.getDateDebut());
    }
    
    private boolean hasResourceOverlap(Intervention intervention1, Intervention intervention2) {
        if (intervention1.getRessources() == null || intervention2.getRessources() == null) {
            return false;
        }
        
        return intervention1.getRessources().stream()
                .anyMatch(r1 -> intervention2.getRessources().stream()
                        .anyMatch(r2 -> r1.getId().equals(r2.getId())));
    }
} 
