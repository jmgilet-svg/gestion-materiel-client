// MockDataManager.java
package com.materiel.client.mock;

import com.materiel.client.model.Resource;
import com.materiel.client.model.Intervention;
import com.materiel.client.model.Client;
import com.materiel.client.model.Devis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Gestionnaire des données Mock persistées en JSON
 */
public class MockDataManager {
    
    private static MockDataManager instance;
    private final ObjectMapper objectMapper;
    private final Path dataDirectory;
    
    private static final String RESOURCES_FILE = "resources.json";
    private static final String INTERVENTIONS_FILE = "interventions.json";
    private static final String CLIENTS_FILE = "clients.json";
    private static final String DEVIS_FILE = "devis.json";
    
    private MockDataManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        // Répertoire de données dans le dossier utilisateur
        this.dataDirectory = Paths.get(System.getProperty("user.home"), ".gestion-materiel", "data");
        
        try {
            Files.createDirectories(dataDirectory);
            initializeDefaultData();
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le répertoire de données", e);
        }
    }
    
    public static MockDataManager getInstance() {
        if (instance == null) {
            instance = new MockDataManager();
        }
        return instance;
    }
    
    private void initializeDefaultData() {
        // Initialiser avec des données par défaut si les fichiers n'existent pas
        if (!Files.exists(dataDirectory.resolve(RESOURCES_FILE))) {
            saveResources(createDefaultResources());
        }
        if (!Files.exists(dataDirectory.resolve(CLIENTS_FILE))) {
            saveClients(createDefaultClients());
        }
        if (!Files.exists(dataDirectory.resolve(INTERVENTIONS_FILE))) {
            saveInterventions(createDefaultInterventions());
        }
        if (!Files.exists(dataDirectory.resolve(DEVIS_FILE))) {
            saveDevis(createDefaultDevis());
        }
    }
    
    private List<Resource> createDefaultResources() {
        List<Resource> resources = new ArrayList<>();
        
        // Grues
        Resource grue1 = new Resource(1L, "Grue 35T Liebherr", Resource.ResourceType.GRUE);
        grue1.setDescription("Grue mobile 35 tonnes, flèche 40m");
        grue1.setSpecifications("{\"capacite\": \"35T\", \"hauteur\": \"40m\", \"marque\": \"Liebherr\"}");
        
        Resource grue2 = new Resource(2L, "Grue 50T Grove", Resource.ResourceType.GRUE);
        grue2.setDescription("Grue mobile 50 tonnes, flèche 45m");
        grue2.setSpecifications("{\"capacite\": \"50T\", \"hauteur\": \"45m\", \"marque\": \"Grove\"}");
        
        Resource grue3 = new Resource(3L, "Grue 25T Terex", Resource.ResourceType.GRUE);
        grue3.setDescription("Grue mobile 25 tonnes, flèche 35m");
        grue3.setSpecifications("{\"capacite\": \"25T\", \"hauteur\": \"35m\", \"marque\": \"Terex\"}");
        
        // Camions
        Resource camion1 = new Resource(4L, "Camion Plateau Mercedes", Resource.ResourceType.CAMION);
        camion1.setDescription("Camion plateau 19T, PTAC 26T");
        camion1.setSpecifications("{\"type\": \"Plateau\", \"poids\": \"26T\", \"marque\": \"Mercedes\"}");
        
        Resource camion2 = new Resource(5L, "Camion Benne Volvo", Resource.ResourceType.CAMION);
        camion2.setDescription("Camion benne basculante 20m³");
        camion2.setSpecifications("{\"type\": \"Benne\", \"volume\": \"20m³\", \"marque\": \"Volvo\"}");
        
        Resource camion3 = new Resource(6L, "Camion Porte-Engin Scania", Resource.ResourceType.CAMION);
        camion3.setDescription("Porte-engin 30T utile");
        camion3.setSpecifications("{\"type\": \"Porte-engin\", \"charge\": \"30T\", \"marque\": \"Scania\"}");
        
        // Chauffeurs
        Resource chauffeur1 = new Resource(7L, "Jean Dupont", Resource.ResourceType.CHAUFFEUR);
        chauffeur1.setDescription("Chauffeur poids lourd + grue, 15 ans d'expérience");
        chauffeur1.setSpecifications("{\"permis\": \"CE\", \"caces\": \"R490\", \"experience\": \"15 ans\"}");
        
        Resource chauffeur2 = new Resource(8L, "Marc Martin", Resource.ResourceType.CHAUFFEUR);
        chauffeur2.setDescription("Chauffeur spécialisé transport exceptionnel");
        chauffeur2.setSpecifications("{\"permis\": \"CE\", \"specialite\": \"Convoi exceptionnel\", \"experience\": \"12 ans\"}");
        
        Resource chauffeur3 = new Resource(9L, "Pierre Dubois", Resource.ResourceType.CHAUFFEUR);
        chauffeur3.setDescription("Chauffeur grue mobile, CACES à jour");
        chauffeur3.setSpecifications("{\"permis\": \"C\", \"caces\": \"R490\", \"experience\": \"8 ans\"}");
        
        // Main d'œuvre
        Resource mainOeuvre1 = new Resource(10L, "Équipe Montage", Resource.ResourceType.MAIN_OEUVRE);
        mainOeuvre1.setDescription("Équipe de 3 personnes spécialisée montage/démontage");
        mainOeuvre1.setSpecifications("{\"nombre\": 3, \"specialite\": \"Montage industriel\"}");
        
        Resource mainOeuvre2 = new Resource(11L, "Équipe Manutention", Resource.ResourceType.MAIN_OEUVRE);
        mainOeuvre2.setDescription("Équipe de manutention et arrimage");
        mainOeuvre2.setSpecifications("{\"nombre\": 4, \"specialite\": \"Manutention lourde\"}");
        
        // Ressources génériques
        Resource treuil1 = new Resource(12L, "Treuil 10T Électrique", Resource.ResourceType.RESSOURCE_GENERIQUE);
        treuil1.setDescription("Treuil électrique 10 tonnes avec télécommande");
        treuil1.setSpecifications("{\"type\": \"Treuil\", \"capacite\": \"10T\", \"alimentation\": \"380V\"}");
        
        Resource treuil2 = new Resource(13L, "Treuil 5T Hydraulique", Resource.ResourceType.RESSOURCE_GENERIQUE);
        treuil2.setDescription("Treuil hydraulique 5 tonnes compact");
        treuil2.setSpecifications("{\"type\": \"Treuil\", \"capacite\": \"5T\", \"alimentation\": \"Hydraulique\"}");
        
        Resource compresseur = new Resource(14L, "Compresseur Mobile 300L", Resource.ResourceType.RESSOURCE_GENERIQUE);
        compresseur.setDescription("Compresseur mobile diesel 300L/min");
        compresseur.setSpecifications("{\"type\": \"Compresseur\", \"debit\": \"300L/min\", \"carburant\": \"Diesel\"}");
        
        Resource generateur = new Resource(15L, "Générateur 100 kVA", Resource.ResourceType.RESSOURCE_GENERIQUE);
        generateur.setDescription("Générateur électrogène 100 kVA diesel");
        generateur.setSpecifications("{\"type\": \"Générateur\", \"puissance\": \"100kVA\", \"carburant\": \"Diesel\"}");
        
        resources.addAll(Arrays.asList(
            grue1, grue2, grue3,
            camion1, camion2, camion3,
            chauffeur1, chauffeur2, chauffeur3,
            mainOeuvre1, mainOeuvre2,
            treuil1, treuil2, compresseur, generateur
        ));
        
        return resources;
    }
    
    private List<Client> createDefaultClients() {
        List<Client> clients = new ArrayList<>();
        
        Client client1 = new Client(1L, "BTP Construction SARL");
        client1.setAdresse("123 Avenue des Bâtisseurs, 75020 Paris");
        client1.setTelephone("01.44.55.66.77");
        client1.setEmail("contact@btp-construction.fr");
        client1.setSiret("12345678901234");
        
        Client client2 = new Client(2L, "Entreprise Durand");
        client2.setAdresse("45 Rue de la Paix, 69003 Lyon");
        client2.setTelephone("04.78.90.12.34");
        client2.setEmail("durand@entreprise.fr");
        client2.setSiret("23456789012345");
        
        Client client3 = new Client(3L, "Travaux Publics Lyon");
        client3.setAdresse("67 Boulevard du Rhône, 69002 Lyon");
        client3.setTelephone("04.72.41.85.96");
        client3.setEmail("info@tp-lyon.fr");
        client3.setSiret("34567890123456");
        
        Client client4 = new Client(4L, "Marseille BTP");
        client4.setAdresse("89 La Canebière, 13001 Marseille");
        client4.setTelephone("04.91.23.45.67");
        client4.setEmail("contact@marseille-btp.fr");
        client4.setSiret("45678901234567");
        
        Client client5 = new Client(5L, "Constructions du Nord");
        client5.setAdresse("12 Place de la République, 59000 Lille");
        client5.setTelephone("03.20.15.78.90");
        client5.setEmail("nord@constructions.fr");
        client5.setSiret("56789012345678");
        
        clients.addAll(Arrays.asList(client1, client2, client3, client4, client5));
        
        return clients;
    }
    
    private List<Intervention> createDefaultInterventions() {
        // Retourner une liste vide pour commencer
        // Les interventions seront créées via l'interface
        return new ArrayList<>();
    }
    
    private List<Devis> createDefaultDevis() {
        List<Devis> devisList = new ArrayList<>();
        List<Client> clients = createDefaultClients();
        
        if (!clients.isEmpty()) {
            // Devis 1 - Envoyé
            Devis devis1 = new Devis();
            devis1.setId(1L);
            devis1.setNumero("DEV-20241208-001");
            devis1.setClient(clients.get(0)); // BTP Construction
            devis1.setDateCreation(LocalDate.now().minusDays(5));
            devis1.setDateValidite(LocalDate.now().plusDays(25));
            devis1.setStatut(Devis.StatutDevis.ENVOYE);
            devis1.setVersion(1);
            devis1.setMontantHT(new BigDecimal("2500.00"));
            devis1.setMontantTVA(new BigDecimal("500.00"));
            devis1.setMontantTTC(new BigDecimal("3000.00"));
            
            // Devis 2 - Accepté
            Devis devis2 = new Devis();
            devis2.setId(2L);
            devis2.setNumero("DEV-20241207-001");
            devis2.setClient(clients.get(1)); // Entreprise Durand
            devis2.setDateCreation(LocalDate.now().minusDays(3));
            devis2.setDateValidite(LocalDate.now().plusDays(27));
            devis2.setStatut(Devis.StatutDevis.ACCEPTE);
            devis2.setVersion(1);
            devis2.setMontantHT(new BigDecimal("4200.00"));
            devis2.setMontantTVA(new BigDecimal("840.00"));
            devis2.setMontantTTC(new BigDecimal("5040.00"));
            
            // Devis 3 - Brouillon
            Devis devis3 = new Devis();
            devis3.setId(3L);
            devis3.setNumero("DEV-20241208-002");
            devis3.setClient(clients.get(2)); // Travaux Publics Lyon
            devis3.setDateCreation(LocalDate.now().minusDays(1));
            devis3.setDateValidite(LocalDate.now().plusDays(29));
            devis3.setStatut(Devis.StatutDevis.BROUILLON);
            devis3.setVersion(1);
            devis3.setMontantHT(new BigDecimal("1800.00"));
            devis3.setMontantTVA(new BigDecimal("360.00"));
            devis3.setMontantTTC(new BigDecimal("2160.00"));
            
            // Devis 4 - Refusé
            Devis devis4 = new Devis();
            devis4.setId(4L);
            devis4.setNumero("DEV-20241205-001");
            devis4.setClient(clients.get(3)); // Marseille BTP
            devis4.setDateCreation(LocalDate.now().minusDays(7));
            devis4.setDateValidite(LocalDate.now().plusDays(23));
            devis4.setStatut(Devis.StatutDevis.REFUSE);
            devis4.setVersion(1);
            devis4.setMontantHT(new BigDecimal("3500.00"));
            devis4.setMontantTVA(new BigDecimal("700.00"));
            devis4.setMontantTTC(new BigDecimal("4200.00"));
            
            // Devis 5 - Expiré
            Devis devis5 = new Devis();
            devis5.setId(5L);
            devis5.setNumero("DEV-20241120-001");
            devis5.setClient(clients.get(4)); // Constructions du Nord
            devis5.setDateCreation(LocalDate.now().minusDays(18));
            devis5.setDateValidite(LocalDate.now().minusDays(3)); // Expiré
            devis5.setStatut(Devis.StatutDevis.EXPIRE);
            devis5.setVersion(1);
            devis5.setMontantHT(new BigDecimal("2800.00"));
            devis5.setMontantTVA(new BigDecimal("560.00"));
            devis5.setMontantTTC(new BigDecimal("3360.00"));
            
            devisList.addAll(Arrays.asList(devis1, devis2, devis3, devis4, devis5));
        }
        
        return devisList;
    }
    
    // Méthodes de persistence
    public List<Resource> getResources() {
        return loadFromFile(RESOURCES_FILE, Resource[].class);
    }
    
    public void saveResources(List<Resource> resources) {
        saveToFile(RESOURCES_FILE, resources);
    }
    
    public List<Intervention> getInterventions() {
        return loadFromFile(INTERVENTIONS_FILE, Intervention[].class);
    }
    
    public void saveInterventions(List<Intervention> interventions) {
        saveToFile(INTERVENTIONS_FILE, interventions);
    }
    
    public List<Client> getClients() {
        return loadFromFile(CLIENTS_FILE, Client[].class);
    }
    
    public void saveClients(List<Client> clients) {
        saveToFile(CLIENTS_FILE, clients);
    }
    
    public List<Devis> getDevis() {
        return loadFromFile(DEVIS_FILE, Devis[].class);
    }
    
    public void saveDevis(List<Devis> devis) {
        saveToFile(DEVIS_FILE, devis);
    }
    
    private <T> List<T> loadFromFile(String filename, Class<T[]> arrayClass) {
        Path filePath = dataDirectory.resolve(filename);
        
        try {
            if (Files.exists(filePath)) {
                byte[] data = Files.readAllBytes(filePath);
                if (data.length > 0) {
                    T[] array = objectMapper.readValue(data, arrayClass);
                    return new ArrayList<>(Arrays.asList(array));
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
    
    private <T> void saveToFile(String filename, List<T> data) {
        Path filePath = dataDirectory.resolve(filename);
        
        try {
            byte[] jsonData = objectMapper.writerWithDefaultPrettyPrinter()
                                        .writeValueAsBytes(data);
            Files.write(filePath, jsonData, StandardOpenOption.CREATE, 
                       StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Données sauvegardées : " + filename + " (" + data.size() + " éléments)");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Path getDataDirectory() {
        return dataDirectory;
    }
    
    /**
     * Réinitialise toutes les données avec les valeurs par défaut
     */
    public void resetAllData() {
        try {
            // Supprimer tous les fichiers existants
            Files.deleteIfExists(dataDirectory.resolve(RESOURCES_FILE));
            Files.deleteIfExists(dataDirectory.resolve(CLIENTS_FILE));
            Files.deleteIfExists(dataDirectory.resolve(INTERVENTIONS_FILE));
            Files.deleteIfExists(dataDirectory.resolve(DEVIS_FILE));
            
            // Recréer avec les données par défaut
            initializeDefaultData();
            
            System.out.println("Toutes les données ont été réinitialisées avec succès.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la réinitialisation des données : " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde de sauvegarde de toutes les données
     */
    public void backupAllData() {
        try {
            Path backupDir = dataDirectory.getParent().resolve("backup-" + LocalDate.now());
            Files.createDirectories(backupDir);
            
            Files.copy(dataDirectory.resolve(RESOURCES_FILE), backupDir.resolve(RESOURCES_FILE));
            Files.copy(dataDirectory.resolve(CLIENTS_FILE), backupDir.resolve(CLIENTS_FILE));
            Files.copy(dataDirectory.resolve(INTERVENTIONS_FILE), backupDir.resolve(INTERVENTIONS_FILE));
            Files.copy(dataDirectory.resolve(DEVIS_FILE), backupDir.resolve(DEVIS_FILE));
            
            System.out.println("Sauvegarde créée dans : " + backupDir);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
    
    /**
     * Affiche les statistiques des données
     */
    public void printStatistics() {
        System.out.println("=== Statistiques des données Mock ===");
        System.out.println("Répertoire : " + dataDirectory);
        System.out.println("Ressources : " + getResources().size());
        System.out.println("Clients : " + getClients().size());
        System.out.println("Interventions : " + getInterventions().size());
        System.out.println("Devis : " + getDevis().size());
        System.out.println("=====================================");
    }
}