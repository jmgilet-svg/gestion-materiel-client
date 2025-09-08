package com.materiel.client.controller.events;

/**
 * Événement de sélection dans le menu
 */
public class MenuSelectionEvent {
    
    private final String selectedMenu;
    
    public MenuSelectionEvent(String selectedMenu) {
        this.selectedMenu = selectedMenu;
    }
    
    public String getSelectedMenu() {
        return selectedMenu;
    }
    
    @Override
    public String toString() {
        return "MenuSelectionEvent{selectedMenu='" + selectedMenu + "'}";
    }
} 
