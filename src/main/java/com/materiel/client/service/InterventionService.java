package com.materiel.client.service;

import com.materiel.client.model.Intervention;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface pour les services de gestion des interventions
 */
public interface InterventionService {
    
    List<Intervention> getAllInterventions();
    List<Intervention> getInterventionsByDateRange(LocalDate startDate, LocalDate endDate);
    Intervention getInterventionById(Long id);
    Intervention saveIntervention(Intervention intervention);
    void deleteIntervention(Long id);
    
    List<Intervention> getInterventionsByClient(Long clientId);
    List<Intervention> getInterventionsByResource(Long resourceId);
    boolean hasConflict(Intervention intervention);
} 
