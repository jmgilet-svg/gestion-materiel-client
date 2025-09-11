package com.materiel.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration globale de l'application (Singleton)
 */
public class AppConfig {
    private static AppConfig instance;

    private DataMode dataMode;
    private String apiBaseUrl;
    private String apiToken;
    private String apiBasicUser;
    private String apiBasicPass;

    private AppConfig() {
        loadConfiguration();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            // ignore, use defaults
        }

        String modeProp = System.getProperty("app.mode", props.getProperty("app.mode", "mock"));
        this.dataMode = "backend".equalsIgnoreCase(modeProp) ? DataMode.BACKEND_API : DataMode.MOCK_JSON;

        this.apiBaseUrl = System.getProperty("api.baseUrl", props.getProperty("api.baseUrl", "http://localhost:8080"));
        this.apiToken = System.getProperty("api.token", props.getProperty("api.token"));
        this.apiBasicUser = System.getProperty("api.basic.user", props.getProperty("api.basic.user"));
        this.apiBasicPass = System.getProperty("api.basic.pass", props.getProperty("api.basic.pass"));
    }

    public DataMode getDataMode() { return dataMode; }
    public void setDataMode(DataMode dataMode) { this.dataMode = dataMode; }

    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }

    public String getApiToken() { return apiToken; }
    public String getApiBasicUser() { return apiBasicUser; }
    public String getApiBasicPass() { return apiBasicPass; }

    public boolean isBackendMode() { return dataMode == DataMode.BACKEND_API; }
    public boolean isMockMode() { return dataMode == DataMode.MOCK_JSON; }

    /** Reset singleton (utile pour les tests) */
    public static void reset() { instance = null; }
}
