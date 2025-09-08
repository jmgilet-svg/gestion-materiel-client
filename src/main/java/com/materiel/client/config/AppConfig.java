package com.materiel.client.config;

import java.util.prefs.Preferences;

/**
 * Configuration globale de l'application (Singleton)
 */
public class AppConfig {
    private static AppConfig instance;
    private static final String PREF_DATA_MODE = "dataMode";
    private static final String PREF_API_BASE_URL = "apiBaseUrl";
    
    private final Preferences prefs;
    private DataMode dataMode;
    private String apiBaseUrl;
    
    private AppConfig() {
        prefs = Preferences.userNodeForPackage(AppConfig.class);
        loadConfiguration();
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        String mode = prefs.get(PREF_DATA_MODE, DataMode.MOCK_JSON.name());
        dataMode = DataMode.valueOf(mode);
        apiBaseUrl = prefs.get(PREF_API_BASE_URL, "http://localhost:8080");
    }
    
    public void saveConfiguration() {
        prefs.put(PREF_DATA_MODE, dataMode.name());
        prefs.put(PREF_API_BASE_URL, apiBaseUrl);
    }
    
    // Getters & Setters
    public DataMode getDataMode() { return dataMode; }
    public void setDataMode(DataMode dataMode) { 
        this.dataMode = dataMode; 
        saveConfiguration();
    }
    
    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { 
        this.apiBaseUrl = apiBaseUrl; 
        saveConfiguration();
    }
    
    public boolean isBackendMode() {
        return dataMode == DataMode.BACKEND_API;
    }
    
    public boolean isMockMode() {
        return dataMode == DataMode.MOCK_JSON;
    }
}
 
