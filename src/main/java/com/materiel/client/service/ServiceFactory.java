package com.materiel.client.service;

import com.materiel.client.config.AppConfig;
import com.materiel.client.service.impl.ApiClientService;
import com.materiel.client.service.impl.ApiDevisService;
import com.materiel.client.service.impl.ApiInterventionService;
import com.materiel.client.service.impl.ApiResourceService;
import com.materiel.client.service.impl.MockClientService;
import com.materiel.client.service.impl.MockDevisService;
import com.materiel.client.service.impl.MockInterventionService;
import com.materiel.client.service.impl.MockResourceService;

/**
 * Factory pour créer les services selon le mode configuré
 */
public class ServiceFactory {
    
    private static ResourceService resourceService;
    private static InterventionService interventionService;
    private static DevisService devisService;
    private static ClientService clientService;
    
    public static ResourceService getResourceService() {
        if (resourceService == null) {
            AppConfig config = AppConfig.getInstance();
            if (config.isBackendMode()) {
                resourceService = new ApiResourceService();
            } else {
                resourceService = new MockResourceService();
            }
        }
        return resourceService;
    }
    
    public static InterventionService getInterventionService() {
        if (interventionService == null) {
            AppConfig config = AppConfig.getInstance();
            if (config.isBackendMode()) {
                interventionService = new ApiInterventionService();
            } else {
                interventionService = new MockInterventionService();
            }
        }
        return interventionService;
    }
    
    public static DevisService getDevisService() {
        if (devisService == null) {
            AppConfig config = AppConfig.getInstance();
            if (config.isBackendMode()) {
                devisService = new ApiDevisService();
            } else {
                devisService = new MockDevisService();
            }
        }
        return devisService;
    }
    
    public static ClientService getClientService() {
        if (clientService == null) {
            AppConfig config = AppConfig.getInstance();
            if (config.isBackendMode()) {
                clientService = new ApiClientService();
            } else {
                clientService = new MockClientService();
            }
        }
        return clientService;
    }
    
    /**
     * Force la recréation des services (utile lors du changement de mode)
     */
    public static void resetServices() {
        resourceService = null;
        interventionService = null;
        devisService = null;
        clientService = null;
    }
}