package com.materiel.client.controller.events;

import com.materiel.client.model.Intervention;

/**
 * Événements liés aux interventions
 */
public class InterventionEvent {
    
    public enum Type {
        CREATED, UPDATED, DELETED, SELECTED
    }
    
    private final Type type;
    private final Intervention intervention;
    
    public InterventionEvent(Type type, Intervention intervention) {
        this.type = type;
        this.intervention = intervention;
    }
    
    public Type getType() {
        return type;
    }
    
    public Intervention getIntervention() {
        return intervention;
    }
    
    @Override
    public String toString() {
        return "InterventionEvent{type=" + type + ", intervention=" + intervention.getTitre() + "}";
    }
} 
