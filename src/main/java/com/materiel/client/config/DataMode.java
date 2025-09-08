package com.materiel.client.config;

/**
 * Modes de fonctionnement de l'application
 */
public enum DataMode {
    BACKEND_API("Backend API"),
    MOCK_JSON("Mock JSON");
    
    private final String displayName;
    
    DataMode(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 
