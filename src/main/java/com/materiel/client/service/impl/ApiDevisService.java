package com.materiel.client.service.impl;

import com.materiel.client.model.Devis;
import com.materiel.client.service.DevisService;
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

/**
 * Implémentation API du service Devis
 */
public class ApiDevisService implements DevisService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    
    public ApiDevisService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.baseUrl = AppConfig.getInstance().getApiBaseUrl() + "/api";
    }
    
    @Override
    public List<Devis> getAllDevis() {
        try {
            String json = makeApiCall("/devis");
            // TODO: Parser et mapper les devis du backend
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des devis", e);
        }
    }
    
    @Override
    public Devis getDevisById(Long id) {
        try {
            String json = makeApiCall("/devis/" + id);
            // TODO: Parser et mapper
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du devis", e);
        }
    }
    
    @Override
    public Devis getDevisByNumero(String numero) {
        try {
            String json = makeApiCall("/devis/numero/" + numero);
            // TODO: Parser et mapper
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du devis par numéro", e);
        }
    }
    
    @Override
    public Devis saveDevis(Devis devis) {
        try {
            String json = objectMapper.writeValueAsString(devis);
            String response;
            
            if (devis.getId() == null) {
                response = makeApiPost("/devis", json);
            } else {
                response = makeApiPut("/devis/" + devis.getId(), json);
            }
            
            // TODO: Parser la réponse
            return devis; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du devis", e);
        }
    }
    
    @Override
    public void deleteDevis(Long id) {
        try {
            makeApiDelete("/devis/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du devis", e);
        }
    }
    
    @Override
    public List<Devis> getDevisByClient(Long clientId) {
        try {
            String json = makeApiCall("/devis/client/" + clientId);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des devis client", e);
        }
    }
    
    @Override
    public List<Devis> getDevisByStatut(Devis.StatutDevis statut) {
        try {
            String json = makeApiCall("/devis/statut/" + statut.name());
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des devis par statut", e);
        }
    }
    
    @Override
    public List<Devis> getDevisByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String endpoint = String.format("/devis/dates?debut=%s&fin=%s", 
                                          startDate.format(formatter), 
                                          endDate.format(formatter));
            String json = makeApiCall(endpoint);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des devis par date", e);
        }
    }
    
    @Override
    public Devis reviserDevis(Long devisId) {
        try {
            String response = makeApiPost("/devis/" + devisId + "/reviser", "");
            // TODO: Parser la réponse
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la révision du devis", e);
        }
    }
    
    @Override
    public void transformerEnBonCommande(Long devisId) {
        try {
            makeApiPost("/devis/" + devisId + "/transformer-bon-commande", "");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la transformation en bon de commande", e);
        }
    }
    
    @Override
    public void marquerExpires() {
        try {
            makeApiPost("/devis/marquer-expires", "");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du marquage des devis expirés", e);
        }
    }
    
    @Override
    public List<Devis> searchDevis(String searchTerm) {
        try {
            String endpoint = "/devis/search?q=" + java.net.URLEncoder.encode(searchTerm, "UTF-8");
            String json = makeApiCall(endpoint);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de devis", e);
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