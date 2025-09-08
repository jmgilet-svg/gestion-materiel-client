package com.materiel.client.controller;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Bus d'événements pour la communication entre composants
 */
public class EventBus {
    
    private static EventBus instance;
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();
    
    private EventBus() {}
    
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
    
    /**
     * S'abonner à un type d'événement
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                 .add((Consumer<Object>) listener);
    }
    
    /**
     * Publier un événement
     */
    public void publish(Object event) {
        Class<?> eventType = event.getClass();
        List<Consumer<Object>> eventListeners = listeners.get(eventType);
        
        if (eventListeners != null) {
            for (Consumer<Object> listener : eventListeners) {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement de l'événement: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Se désabonner d'un type d'événement
     */
    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<Object>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            if (eventListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }
    
    /**
     * Nettoyer tous les listeners
     */
    public void clear() {
        listeners.clear();
    }
} 
