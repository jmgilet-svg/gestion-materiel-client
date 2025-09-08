package com.materiel.client.service;

import com.materiel.client.config.AppConfig;
import com.materiel.client.service.impl.ApiResourceService;
import com.materiel.client.service.impl.MockResourceService;
import com.materiel.client.service.impl.ApiInterventionService;
import com.materiel.client.service.impl.MockInterventionService;

/**
 * Factory pour créer les services selon le mode configuré
 */
public class ServiceFactory {
    
    private static ResourceService resourceService;
    private static InterventionService interventionService;
    
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
    
    /**
     * Force la recréation des services (utile lors du changement de mode)
     */
    public static void resetServices() {
        resourceService = null;
        interventionService = null;
    }
} 
