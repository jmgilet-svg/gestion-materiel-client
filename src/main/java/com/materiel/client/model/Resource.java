package com.materiel.client.model;

import java.time.LocalDateTime;

/**
 * Modèle pour les ressources (grues, camions, etc.)
 */
public class Resource {
    
    public enum ResourceType {
        GRUE("Grue", "#3B82F6"),
        CAMION("Camion", "#F97316"),
        CHAUFFEUR("Chauffeur", "#10B981"),
        MAIN_OEUVRE("Main d'œuvre", "#8B5CF6"),
        RESSOURCE_GENERIQUE("Ressource", "#64748B");
        
        private final String displayName;
        private final String color;
        
        ResourceType(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private Long id;
    private String nom;
    private ResourceType type;
    private String description;
    private boolean disponible;
    private String specifications; // JSON pour les specs spécifiques
    
    // Constructors
    public Resource() {}
    
    public Resource(Long id, String nom, ResourceType type) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.disponible = true;
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
    
    @Override
    public String toString() {
        return nom + " (" + type.getDisplayName() + ")";
    }
} 
