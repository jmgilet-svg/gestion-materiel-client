package com.materiel.client.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle pour les bons de commande
 */
public class Commande {
    
    public enum StatutCommande {
        BROUILLON("Brouillon", "#64748B"),
        CONFIRMEE("Confirmée", "#3B82F6"),
        EN_PREPARATION("En préparation", "#F97316"),
        PRETE("Prête", "#10B981"),
        LIVREE("Livrée", "#059669"),
        ANNULEE("Annulée", "#EF4444");
        
        private final String displayName;
        private final String color;
        
        StatutCommande(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private Long id;
    private String numero;
    private LocalDate dateCreation;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonEffective;
    private Client client;
    private Devis devisOrigine; // Référence au devis source
    private StatutCommande statut;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String adresseLivraison;
    private String commentaires;
    private String responsablePreparation;
    
    // Constructors
    public Commande() {
        this.statut = StatutCommande.BROUILLON;
        this.dateCreation = LocalDate.now();
        this.montantHT = BigDecimal.ZERO;
        this.montantTVA = BigDecimal.ZERO;
        this.montantTTC = BigDecimal.ZERO;
    }
    
    public Commande(Devis devisSource) {
        this();
        this.devisOrigine = devisSource;
        this.client = devisSource.getClient();
        this.montantHT = devisSource.getMontantHT();
        this.montantTVA = devisSource.getMontantTVA();
        this.montantTTC = devisSource.getMontantTTC();
        this.statut = StatutCommande.CONFIRMEE;
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDate getDateLivraisonPrevue() { return dateLivraisonPrevue; }
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) { this.dateLivraisonPrevue = dateLivraisonPrevue; }
    
    public LocalDate getDateLivraisonEffective() { return dateLivraisonEffective; }
    public void setDateLivraisonEffective(LocalDate dateLivraisonEffective) { this.dateLivraisonEffective = dateLivraisonEffective; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public Devis getDevisOrigine() { return devisOrigine; }
    public void setDevisOrigine(Devis devisOrigine) { this.devisOrigine = devisOrigine; }
    
    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }
    
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }
    
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }
    
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    
    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
    
    public String getResponsablePreparation() { return responsablePreparation; }
    public void setResponsablePreparation(String responsablePreparation) { this.responsablePreparation = responsablePreparation; }
    
    /**
     * Vérifier si la commande est en retard
     */
    public boolean isEnRetard() {
        return dateLivraisonPrevue != null && 
               dateLivraisonEffective == null && 
               LocalDate.now().isAfter(dateLivraisonPrevue) &&
               statut != StatutCommande.LIVREE &&
               statut != StatutCommande.ANNULEE;
    }
    
    /**
     * Calculer le délai de livraison en jours
     */
    public long getDelaiLivraison() {
        if (dateCreation == null || dateLivraisonEffective == null) {
            return -1;
        }
        return dateCreation.until(dateLivraisonEffective).getDays();
    }
    
    /**
     * Vérifier si la commande peut être modifiée
     */
    public boolean peutEtreModifiee() {
        return statut == StatutCommande.BROUILLON || statut == StatutCommande.CONFIRMEE;
    }
    
    /**
     * Vérifier si la commande peut être annulée
     */
    public boolean peutEtreAnnulee() {
        return statut != StatutCommande.LIVREE && statut != StatutCommande.ANNULEE;
    }
    
    @Override
    public String toString() {
        return numero + " (" + client + ")";
    }
}