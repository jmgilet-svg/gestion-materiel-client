package com.materiel.client.service.impl;

import com.materiel.client.model.Resource;
import com.materiel.client.service.ResourceService;
import com.materiel.client.mock.MockDataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implémentation Mock du service Resource
 */
public class MockResourceService implements ResourceService {
    
    private final MockDataManager mockDataManager;
    
    public MockResourceService() {
        this.mockDataManager = MockDataManager.getInstance();
    }
    
    @Override
    public List<Resource> getAllResources() {
        return mockDataManager.getResources();
    }
    
    @Override
    public List<Resource> getAvailableResources(LocalDate startDate, LocalDate endDate) {
        // Pour le mock, on retourne toutes les ressources disponibles
        return getAllResources().stream()
                .filter(Resource::isDisponible)
                .collect(Collectors.toList());
    }
    
    @Override
    public Resource getResourceById(Long id) {
        return getAllResources().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Resource saveResource(Resource resource) {
        List<Resource> resources = new ArrayList<>(getAllResources());
        
        if (resource.getId() == null) {
            // Nouvelle ressource
            Long maxId = resources.stream()
                    .mapToLong(Resource::getId)
                    .max()
                    .orElse(0L);
            resource.setId(maxId + 1);
            resources.add(resource);
        } else {
            // Mise à jour
            for (int i = 0; i < resources.size(); i++) {
                if (resources.get(i).getId().equals(resource.getId())) {
                    resources.set(i, resource);
                    break;
                }
            }
        }
        
        mockDataManager.saveResources(resources);
        return resource;
    }
    
    @Override
    public void deleteResource(Long id) {
        List<Resource> resources = getAllResources().stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        mockDataManager.saveResources(resources);
    }
    
    @Override
    public List<Resource> getResourcesByType(Resource.ResourceType type) {
        return getAllResources().stream()
                .filter(r -> r.getType() == type)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isResourceAvailable(Long resourceId, LocalDate startDate, LocalDate endDate) {
        // Pour le mock, on simule une vérification simple
        Resource resource = getResourceById(resourceId);
        return resource != null && resource.isDisponible();
    }
} 
