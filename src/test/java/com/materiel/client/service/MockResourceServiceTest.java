package com.materiel.client.service;

import com.materiel.client.model.Resource;
import com.materiel.client.service.impl.MockResourceService;
import com.materiel.client.mock.MockDataManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour MockResourceService
 */
@DisplayName("Tests MockResourceService")
class MockResourceServiceTest {
    
    private MockResourceService resourceService;
    private MockDataManager mockDataManager;
    
    @BeforeEach
    void setUp() {
        resourceService = new MockResourceService();
        mockDataManager = MockDataManager.getInstance();
        
        // Nettoyer les données pour les tests
        mockDataManager.resetAllData();
    }
    
    @AfterEach
    void tearDown() {
        // Remettre les données par défaut après les tests
        mockDataManager.resetAllData();
    }
    
    @Test
    @DisplayName("Devrait récupérer toutes les ressources")
    void shouldGetAllResources() {
        // When
        List<Resource> resources = resourceService.getAllResources();
        
        // Then
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
        
        // Vérifier qu'on a des ressources de différents types
        boolean hasGrue = resources.stream().anyMatch(r -> r.getType() == Resource.ResourceType.GRUE);
        boolean hasCamion = resources.stream().anyMatch(r -> r.getType() == Resource.ResourceType.CAMION);
        boolean hasChauffeur = resources.stream().anyMatch(r -> r.getType() == Resource.ResourceType.CHAUFFEUR);
        
        assertTrue(hasGrue, "Devrait avoir au moins une grue");
        assertTrue(hasCamion, "Devrait avoir au moins un camion");
        assertTrue(hasChauffeur, "Devrait avoir au moins un chauffeur");
    }
    
    @Test
    @DisplayName("Devrait récupérer les ressources disponibles")
    void shouldGetAvailableResources() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        // When
        List<Resource> availableResources = resourceService.getAvailableResources(startDate, endDate);
        
        // Then
        assertNotNull(availableResources);
        
        // Toutes les ressources retournées devraient être disponibles
        for (Resource resource : availableResources) {
            assertTrue(resource.isDisponible(), 
                "La ressource " + resource.getNom() + " devrait être disponible");
        }
    }
    
    @Test
    @DisplayName("Devrait récupérer une ressource par ID")
    void shouldGetResourceById() {
        // Given
        List<Resource> allResources = resourceService.getAllResources();
        assertFalse(allResources.isEmpty(), "Il faut au moins une ressource pour ce test");
        
        Resource expectedResource = allResources.get(0);
        Long resourceId = expectedResource.getId();
        
        // When
        Resource foundResource = resourceService.getResourceById(resourceId);
        
        // Then
        assertNotNull(foundResource);
        assertEquals(expectedResource.getId(), foundResource.getId());
        assertEquals(expectedResource.getNom(), foundResource.getNom());
        assertEquals(expectedResource.getType(), foundResource.getType());
    }
    
    @Test
    @DisplayName("Devrait retourner null pour un ID inexistant")
    void shouldReturnNullForNonExistentId() {
        // Given
        Long nonExistentId = 99999L;
        
        // When
        Resource foundResource = resourceService.getResourceById(nonExistentId);
        
        // Then
        assertNull(foundResource);
    }
    
    @Test
    @DisplayName("Devrait sauvegarder une nouvelle ressource")
    void shouldSaveNewResource() {
        // Given
        Resource newResource = new Resource();
        newResource.setNom("Test Grue Nouvelle");
        newResource.setType(Resource.ResourceType.GRUE);
        newResource.setDescription("Grue de test pour les tests unitaires");
        newResource.setDisponible(true);
        
        int initialCount = resourceService.getAllResources().size();
        
        // When
        Resource savedResource = resourceService.saveResource(newResource);
        
        // Then
        assertNotNull(savedResource);
        assertNotNull(savedResource.getId());
        assertEquals("Test Grue Nouvelle", savedResource.getNom());
        assertEquals(Resource.ResourceType.GRUE, savedResource.getType());
        
        // Vérifier que la ressource a été ajoutée
        List<Resource> allResources = resourceService.getAllResources();
        assertEquals(initialCount + 1, allResources.size());
        
        // Vérifier qu'on peut la retrouver
        Resource foundResource = resourceService.getResourceById(savedResource.getId());
        assertNotNull(foundResource);
        assertEquals(savedResource.getNom(), foundResource.getNom());
    }
    
    @Test
    @DisplayName("Devrait mettre à jour une ressource existante")
    void shouldUpdateExistingResource() {
        // Given
        List<Resource> resources = resourceService.getAllResources();
        assertFalse(resources.isEmpty(), "Il faut au moins une ressource pour ce test");
        
        Resource existingResource = resources.get(0);
        Long resourceId = existingResource.getId();
        String originalName = existingResource.getNom();
        String newName = "Nom Modifié Test";
        
        existingResource.setNom(newName);
        existingResource.setDescription("Description modifiée");
        
        // When
        Resource updatedResource = resourceService.saveResource(existingResource);
        
        // Then
        assertNotNull(updatedResource);
        assertEquals(resourceId, updatedResource.getId());
        assertEquals(newName, updatedResource.getNom());
        assertEquals("Description modifiée", updatedResource.getDescription());
        assertNotEquals(originalName, updatedResource.getNom());
        
        // Vérifier que la modification a bien été persistée
        Resource foundResource = resourceService.getResourceById(resourceId);
        assertEquals(newName, foundResource.getNom());
    }
    
    @Test
    @DisplayName("Devrait supprimer une ressource")
    void shouldDeleteResource() {
        // Given
        Resource newResource = new Resource();
        newResource.setNom("Ressource à Supprimer");
        newResource.setType(Resource.ResourceType.RESSOURCE_GENERIQUE);
        
        Resource savedResource = resourceService.saveResource(newResource);
        Long resourceId = savedResource.getId();
        
        // Vérifier que la ressource existe
        assertNotNull(resourceService.getResourceById(resourceId));
        
        int countBeforeDelete = resourceService.getAllResources().size();
        
        // When
        resourceService.deleteResource(resourceId);
        
        // Then
        // Vérifier que la ressource a été supprimée
        assertNull(resourceService.getResourceById(resourceId));
        
        // Vérifier que le nombre total a diminué
        List<Resource> allResources = resourceService.getAllResources();
        assertEquals(countBeforeDelete - 1, allResources.size());
    }
    
    @Test
    @DisplayName("Devrait filtrer les ressources par type")
    void shouldGetResourcesByType() {
        // Given
        Resource.ResourceType targetType = Resource.ResourceType.GRUE;
        
        // When
        List<Resource> grues = resourceService.getResourcesByType(targetType);
        
        // Then
        assertNotNull(grues);
        
        // Toutes les ressources retournées devraient être du bon type
        for (Resource resource : grues) {
            assertEquals(targetType, resource.getType(),
                "La ressource " + resource.getNom() + " devrait être de type " + targetType);
        }
        
        // Vérifier qu'on a bien des grues dans les données par défaut
        List<Resource> allResources = resourceService.getAllResources();
        long expectedGrueCount = allResources.stream()
                .filter(r -> r.getType() == Resource.ResourceType.GRUE)
                .count();
        
        assertEquals(expectedGrueCount, grues.size());
    }
    
    @Test
    @DisplayName("Devrait vérifier la disponibilité d'une ressource")
    void shouldCheckResourceAvailability() {
        // Given
        List<Resource> resources = resourceService.getAllResources();
        assertFalse(resources.isEmpty(), "Il faut au moins une ressource pour ce test");
        
        Resource resource = resources.get(0);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        // When
        boolean isAvailable = resourceService.isResourceAvailable(resource.getId(), startDate, endDate);
        
        // Then
        // Dans l'implémentation Mock, cela devrait retourner le statut de disponibilité
        assertEquals(resource.isDisponible(), isAvailable);
    }
    
    @Test
    @DisplayName("Devrait gérer les ressources indisponibles")
    void shouldHandleUnavailableResources() {
        // Given
        Resource unavailableResource = new Resource();
        unavailableResource.setNom("Ressource Indisponible");
        unavailableResource.setType(Resource.ResourceType.CAMION);
        unavailableResource.setDisponible(false);
        
        Resource savedResource = resourceService.saveResource(unavailableResource);
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        // When
        List<Resource> availableResources = resourceService.getAvailableResources(startDate, endDate);
        boolean isAvailable = resourceService.isResourceAvailable(savedResource.getId(), startDate, endDate);
        
        // Then
        assertFalse(isAvailable, "La ressource devrait être indisponible");
        
        // La ressource ne devrait pas être dans la liste des disponibles
        boolean foundInAvailable = availableResources.stream()
                .anyMatch(r -> r.getId().equals(savedResource.getId()));
        assertFalse(foundInAvailable, "La ressource indisponible ne devrait pas être dans les disponibles");
    }
    
    @Test
    @DisplayName("Devrait avoir des données par défaut cohérentes")
    void shouldHaveConsistentDefaultData() {
        // When
        List<Resource> resources = resourceService.getAllResources();
        
        // Then
        assertNotNull(resources);
        assertFalse(resources.isEmpty(), "Il devrait y avoir des données par défaut");
        
        // Vérifier que chaque ressource a les champs obligatoires
        for (Resource resource : resources) {
            assertNotNull(resource.getId(), "Chaque ressource devrait avoir un ID");
            assertNotNull(resource.getNom(), "Chaque ressource devrait avoir un nom");
            assertNotNull(resource.getType(), "Chaque ressource devrait avoir un type");
            assertFalse(resource.getNom().trim().isEmpty(), "Le nom ne devrait pas être vide");
        }
        
        // Vérifier qu'on a une variété de types
        long distinctTypes = resources.stream()
                .map(Resource::getType)
                .distinct()
                .count();
        
        assertTrue(distinctTypes >= 3, "Il devrait y avoir au moins 3 types de ressources différents");
    }
    
    @Test
    @DisplayName("Les IDs devraient être uniques et croissants pour les nouvelles ressources")
    void shouldHaveUniqueIncrementingIds() {
        // Given
        Resource resource1 = new Resource();
        resource1.setNom("Test Resource 1");
        resource1.setType(Resource.ResourceType.RESSOURCE_GENERIQUE);
        
        Resource resource2 = new Resource();
        resource2.setNom("Test Resource 2");
        resource2.setType(Resource.ResourceType.RESSOURCE_GENERIQUE);
        
        // When
        Resource saved1 = resourceService.saveResource(resource1);
        Resource saved2 = resourceService.saveResource(resource2);
        
        // Then
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
        assertTrue(saved2.getId() > saved1.getId(), "Les IDs devraient être croissants");
    }
} 
