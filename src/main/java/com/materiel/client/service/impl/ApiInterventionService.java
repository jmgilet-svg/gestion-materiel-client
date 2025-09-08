package com.materiel.client.service.impl;

import com.materiel.client.model.Intervention;
import com.materiel.client.service.InterventionService;
import com.materiel.client.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implémentation API du service Intervention
 */
public class ApiInterventionService implements InterventionService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    
    public ApiInterventionService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.baseUrl = AppConfig.getInstance().getApiBaseUrl() + "/api";
    }
    
    @Override
    public List<Intervention> getAllInterventions() {
        try {
            String json = makeApiCall("/reservations");
            // TODO: Mapper les réservations backend vers les interventions client
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions", e);
        }
    }
    
    @Override
    public List<Intervention> getInterventionsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String endpoint = String.format("/reservations?dateDebut=%s&dateFin=%s", 
                                          startDate.format(formatter), 
                                          endDate.format(formatter));
            String json = makeApiCall(endpoint);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions par date", e);
        }
    }
    
    @Override
    public Intervention getInterventionById(Long id) {
        try {
            String json = makeApiCall("/reservations/" + id);
            // TODO: Parser et mapper
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération de l'intervention", e);
        }
    }
    
    @Override
    public Intervention saveIntervention(Intervention intervention) {
        try {
            String json = objectMapper.writeValueAsString(intervention);
            String response;
            
            if (intervention.getId() == null) {
                response = makeApiPost("/reservations", json);
            } else {
                response = makeApiPut("/reservations/" + intervention.getId(), json);
            }
            
            // TODO: Parser la réponse
            return intervention; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'intervention", e);
        }
    }
    
    @Override
    public void deleteIntervention(Long id) {
        try {
            makeApiDelete("/reservations/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'intervention", e);
        }
    }
    
    @Override
    public List<Intervention> getInterventionsByClient(Long clientId) {
        try {
            String json = makeApiCall("/reservations/client/" + clientId);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions client", e);
        }
    }
    
    @Override
    public List<Intervention> getInterventionsByResource(Long resourceId) {
        try {
            String json = makeApiCall("/reservations/resource/" + resourceId);
            // TODO: Parser et mapper  
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions par ressource", e);
        }
    }
    
    @Override
    public boolean hasConflict(Intervention intervention) {
        try {
            String json = objectMapper.writeValueAsString(intervention);
            String response = makeApiPost("/reservations/check-conflict", json);
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            // En cas d'erreur, considérer qu'il n'y a pas de conflit
            System.err.println("Erreur lors de la vérification de conflit: " + e.getMessage());
            return false;
        }
    }
    
    private String makeApiCall(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                // TODO: Ajouter l'authentification JWT
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API: " + response.statusCode() + " - " + response.body());
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
            if (response.statusCode() == 409) {
                throw new RuntimeException("Conflit de réservation détecté");
            }
            throw new RuntimeException("Erreur API: " + response.statusCode() + " - " + response.body());
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
            if (response.statusCode() == 409) {
                throw new RuntimeException("Conflit de réservation détecté");
            }
            throw new RuntimeException("Erreur API: " + response.statusCode() + " - " + response.body());
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
            throw new RuntimeException("Erreur API: " + response.statusCode() + " - " + response.body());
        }
    }
} 
