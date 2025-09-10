package com.materiel.client.service.impl;

import com.materiel.client.model.Resource;
import com.materiel.client.service.ResourceService;
import com.materiel.client.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Implémentation API du service Resource
 */
public class ApiResourceService implements ResourceService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    
    public ApiResourceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.baseUrl = AppConfig.getInstance().getApiBaseUrl() + "/api";
    }
    
    @Override
    public List<Resource> getAllResources() {
        try {
            // Combinaison de tous les types de ressources
            List<Resource> allResources = new ArrayList<>();
            
            // Grues
            String gruesJson = makeApiCall("/grues");
            // TODO: Mapper les entités backend vers les DTOs client
            
            // Camions
            String camionsJson = makeApiCall("/camions");
            // TODO: Mapper les entités backend vers les DTOs client
            
            // Chauffeurs
            String chauffeursJson = makeApiCall("/chauffeurs");
            // TODO: Mapper les entités backend vers les DTOs client
            
            return allResources;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des ressources", e);
        }
    }
    
    @Override
    public List<Resource> getAvailableResources(LocalDate startDate, LocalDate endDate) {
        try {
            String endpoint = String.format("/ressources/disponibles?dateDebut=%sT00:00:00&dateFin=%sT23:59:59", 
                                          startDate, endDate);
            String json = makeApiCall(endpoint);
            // TODO: Parser la réponse JSON et mapper vers Resource
            return getAllResources(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des ressources disponibles", e);
        }
    }
    
    @Override
    public Resource getResourceById(Long id) {
        try {
            String json = makeApiCall("/ressources/" + id);
            // TODO: Parser la réponse JSON
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération de la ressource", e);
        }
    }
    
    @Override
    public Resource saveResource(Resource resource) {
        try {
            String json = objectMapper.writeValueAsString(resource);
            String response;
            
            if (resource.getId() == null) {
                response = makeApiPost("/ressources", json);
            } else {
                response = makeApiPut("/ressources/" + resource.getId(), json);
            }
            
            // TODO: Parser la réponse
            return resource; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la ressource", e);
        }
    }
    
    @Override
    public void deleteResource(Long id) {
        try {
            makeApiDelete("/ressources/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de la ressource", e);
        }
    }
    
    @Override
    public List<Resource> getResourcesByType(Resource.ResourceType type) {
        // TODO: Implémenter selon le type
        return getAllResources().stream()
                .filter(r -> r.getType() == type)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isResourceAvailable(Long resourceId, LocalDate startDate, LocalDate endDate) {
        try {
            String endpoint = String.format("/ressources/%d/disponible?dateDebut=%sT00:00:00&dateFin=%sT23:59:59", 
                                          resourceId, startDate, endDate);
            String response = makeApiCall(endpoint);
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la vérification de disponibilité", e);
        }
    }
    
    private String makeApiCall(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                // TODO: Ajouter l'authentification JWT
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API: " + response.statusCode());
        }
        
        return response.body();
    }
    
    private String makeApiPost(String endpoint, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                // TODO: Ajouter l'authentification JWT
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Erreur API: " + response.statusCode());
        }
        
        return response.body();
    }
    
    private String makeApiPut(String endpoint, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                // TODO: Ajouter l'authentification JWT
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API: " + response.statusCode());
        }
        
        return response.body();
    }
    
    private void makeApiDelete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                // TODO: Ajouter l'authentification JWT
                .DELETE()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Erreur API: " + response.statusCode());
        }
    }
}
 
