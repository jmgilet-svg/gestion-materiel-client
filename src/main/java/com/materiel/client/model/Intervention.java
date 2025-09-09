package com.materiel.client.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modèle pour les interventions/réservations
 */
public class Intervention {
    
    public enum StatutIntervention {
        PLANIFIEE("Planifiée", "#F97316"),
        EN_COURS("En cours", "#3B82F6"),
        TERMINEE("Terminée", "#10B981"),
        ANNULEE("Annulée", "#EF4444");
        
        private final String displayName;
        private final String color;
        
        StatutIntervention(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutIntervention statut;
    private Client client;
    private List<Resource> ressources;
    private String adresseIntervention;
    private String notes;
    
    // Constructors
    public Intervention() {
        this.statut = StatutIntervention.PLANIFIEE;
    }
    
    public Intervention(String titre, LocalDateTime dateDebut, LocalDateTime dateFin, Client client) {
        this();
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.client = client;
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    
    public StatutIntervention getStatut() { return statut; }
    public void setStatut(StatutIntervention statut) { this.statut = statut; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public List<Resource> getRessources() { return ressources; }
    public void setRessources(List<Resource> ressources) { this.ressources = ressources; }
    
    public String getAdresseIntervention() { return adresseIntervention; }
    public void setAdresseIntervention(String adresseIntervention) { this.adresseIntervention = adresseIntervention; }
    
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getHeureDebut() {
        return dateDebut != null ? dateDebut.toLocalTime().toString() : "";
    }
    
    public String getHeureFin() {
        return dateFin != null ? dateFin.toLocalTime().toString() : "";
    }
} 
