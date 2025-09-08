package com.materiel.client.service;

import com.materiel.client.model.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface pour les services de gestion des ressources
 */
public interface ResourceService {
    
    List<Resource> getAllResources();
    List<Resource> getAvailableResources(LocalDate startDate, LocalDate endDate);
    Resource getResourceById(Long id);
    Resource saveResource(Resource resource);
    void deleteResource(Long id);
    
    List<Resource> getResourcesByType(Resource.ResourceType type);
    boolean isResourceAvailable(Long resourceId, LocalDate startDate, LocalDate endDate);
} 
