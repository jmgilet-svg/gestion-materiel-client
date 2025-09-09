package com.materiel.client.service.impl;

import com.materiel.client.model.Client;
import com.materiel.client.service.ClientService;
import com.materiel.client.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

/**
 * Implémentation API du service Client
 */
public class ApiClientService implements ClientService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    
    public ApiClientService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.baseUrl = AppConfig.getInstance().getApiBaseUrl() + "/api";
    }
    
    @Override
    public List<Client> getAllClients() {
        try {
            String json = makeApiCall("/clients");
            // TODO: Parser et mapper les clients du backend
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des clients", e);
        }
    }
    
    @Override
    public Client getClientById(Long id) {
        try {
            String json = makeApiCall("/clients/" + id);
            // TODO: Parser et mapper
            return null; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du client", e);
        }
    }
    
    @Override
    public Client saveClient(Client client) {
        try {
            String json = objectMapper.writeValueAsString(client);
            String response;
            
            if (client.getId() == null) {
                response = makeApiPost("/clients", json);
            } else {
                response = makeApiPut("/clients/" + client.getId(), json);
            }
            
            // TODO: Parser la réponse
            return client; // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du client", e);
        }
    }
    
    @Override
    public void deleteClient(Long id) {
        try {
            makeApiDelete("/clients/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du client", e);
        }
    }
    
    @Override
    public List<Client> searchClients(String searchTerm) {
        try {
            String endpoint = "/clients/search?q=" + java.net.URLEncoder.encode(searchTerm, "UTF-8");
            String json = makeApiCall(endpoint);
            // TODO: Parser et mapper
            return new ArrayList<>(); // Temporaire
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de clients", e);
        }
    }
    
    @Override
    public boolean isClientUsed(Long clientId) {
        try {
            String response = makeApiCall("/clients/" + clientId + "/usage");
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            return false; // En cas d'erreur, considérer que non utilisé
        }
    }
    
    private String makeApiCall(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
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
                .DELETE()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Erreur API: " + response.statusCode());
        }
    }
}