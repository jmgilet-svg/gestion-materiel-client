package com.materiel.client.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle pour les bons de livraison
 */
public class BonLivraison {
    
    public enum StatutBonLivraison {
        PREPARE("Préparé", "#F97316"),
        EN_TRANSPORT("En transport", "#3B82F6"),
        LIVRE("Livré", "#10B981"),
        RETOURNE("Retourné", "#EF4444"),
        ANNULE("Annulé", "#6B7280");
        
        private final String displayName;
        private final String color;
        
        StatutBonLivraison(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private Long id;
    private String numero;
    private LocalDate dateCreation;
    private LocalDateTime dateLivraison;
    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private Client client;
    private Commande commandeOrigine; // Référence à la commande source
    private StatutBonLivraison statut;
    private String adresseLivraison;
    private String chauffeur;
    private String vehicule;
    private String numeroImmatriculation;
    private BigDecimal poidsTotal;
    private String commentairesLivraison;
    private String signatureClient;
    private String personneReceptionnee;
    private boolean livraisonPartielle;
    private String raisonRetour; // Si retourné
    
    // Constructors
    public BonLivraison() {
        this.statut = StatutBonLivraison.PREPARE;
        this.dateCreation = LocalDate.now();
        this.livraisonPartielle = false;
    }
    
    public BonLivraison(Commande commandeSource) {
        this();
        this.commandeOrigine = commandeSource;
        this.client = commandeSource.getClient();
        this.adresseLivraison = commandeSource.getAdresseLivraison();
        this.dateLivraison = commandeSource.getDateLivraisonPrevue().atStartOfDay();
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
    
    public LocalDateTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalDateTime heureDepart) { this.heureDepart = heureDepart; }
    
    public LocalDateTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalDateTime heureArrivee) { this.heureArrivee = heureArrivee; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public Commande getCommandeOrigine() { return commandeOrigine; }
    public void setCommandeOrigine(Commande commandeOrigine) { this.commandeOrigine = commandeOrigine; }
    
    public StatutBonLivraison getStatut() { return statut; }
    public void setStatut(StatutBonLivraison statut) { this.statut = statut; }
    
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    
    public String getChauffeur() { return chauffeur; }
    public void setChauffeur(String chauffeur) { this.chauffeur = chauffeur; }
    
    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    
    public String getNumeroImmatriculation() { return numeroImmatriculation; }
    public void setNumeroImmatriculation(String numeroImmatriculation) { this.numeroImmatriculation = numeroImmatriculation; }
    
    public BigDecimal getPoidsTotal() { return poidsTotal; }
    public void setPoidsTotal(BigDecimal poidsTotal) { this.poidsTotal = poidsTotal; }
    
    public String getCommentairesLivraison() { return commentairesLivraison; }
    public void setCommentairesLivraison(String commentairesLivraison) { this.commentairesLivraison = commentairesLivraison; }
    
    public String getSignatureClient() { return signatureClient; }
    public void setSignatureClient(String signatureClient) { this.signatureClient = signatureClient; }
    
    public String getPersonneReceptionnee() { return personneReceptionnee; }
    public void setPersonneReceptionnee(String personneReceptionnee) { this.personneReceptionnee = personneReceptionnee; }
    
    public boolean isLivraisonPartielle() { return livraisonPartielle; }
    public void setLivraisonPartielle(boolean livraisonPartielle) { this.livraisonPartielle = livraisonPartielle; }
    
    public String getRaisonRetour() { return raisonRetour; }
    public void setRaisonRetour(String raisonRetour) { this.raisonRetour = raisonRetour; }
    
    /**
     * Calculer la durée du transport en minutes
     */
    public long getDureeTransport() {
        if (heureDepart == null || heureArrivee == null) {
            return -1;
        }
        return java.time.Duration.between(heureDepart, heureArrivee).toMinutes();
    }
    
    /**
     * Vérifier si la livraison est en retard
     */
    public boolean isEnRetard() {
        if (dateLivraison == null) return false;
        
        LocalDateTime maintenant = LocalDateTime.now();
        return maintenant.isAfter(dateLivraison) && 
               statut != StatutBonLivraison.LIVRE && 
               statut != StatutBonLivraison.ANNULE;
    }
    
    /**
     * Vérifier si le bon de livraison peut être modifié
     */
    public boolean peutEtreModifie() {
        return statut == StatutBonLivraison.PREPARE;
    }
    
    /**
     * Vérifier si le transport peut être démarré
     */
    public boolean peutDemarrerTransport() {
        return statut == StatutBonLivraison.PREPARE && 
               chauffeur != null && !chauffeur.trim().isEmpty() &&
               vehicule != null && !vehicule.trim().isEmpty();
    }
    
    /**
     * Vérifier si la livraison peut être confirmée
     */
    public boolean peutConfirmerLivraison() {
        return statut == StatutBonLivraison.EN_TRANSPORT;
    }
    
    /**
     * Obtenir un résumé du transport
     */
    public String getResumeTransport() {
        StringBuilder resume = new StringBuilder();
        
        if (chauffeur != null) {
            resume.append("Chauffeur: ").append(chauffeur);
        }
        
        if (vehicule != null) {
            if (resume.length() > 0) resume.append(" • ");
            resume.append("Véhicule: ").append(vehicule);
        }
        
        if (numeroImmatriculation != null) {
            if (resume.length() > 0) resume.append(" • ");
            resume.append("Immat: ").append(numeroImmatriculation);
        }
        
        return resume.toString();
    }
    
    @Override
    public String toString() {
        return numero + " (" + client + ")";
    }
}