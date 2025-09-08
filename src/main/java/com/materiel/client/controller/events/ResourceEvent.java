package com.materiel.client.controller.events;

import com.materiel.client.model.Resource;

/**
 * Événements liés aux ressources
 */
public class ResourceEvent {
    
    public enum Type {
        CREATED, UPDATED, DELETED, SELECTED, AVAILABILITY_CHANGED
    }
    
    private final Type type;
    private final Resource resource;
    
    public ResourceEvent(Type type, Resource resource) {
        this.type = type;
        this.resource = resource;
    }
    
    public Type getType() {
        return type;
    }
    
    public Resource getResource() {
        return resource;
    }
    
    @Override
    public String toString() {
        return "ResourceEvent{type=" + type + ", resource=" + resource.getNom() + "}";
    }
} 
