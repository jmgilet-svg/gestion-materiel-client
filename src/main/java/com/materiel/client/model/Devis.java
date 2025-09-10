package com.materiel.client.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.materiel.client.model.DocumentLine;
import com.materiel.client.util.DocumentTotalsCalculator;

/**
 * Modèle client pour les devis
 */
public class Devis {
    
    public enum StatutDevis {
        BROUILLON("Brouillon", "#64748B"),
        ENVOYE("Envoyé", "#F97316"),
        ACCEPTE("Accepté", "#10B981"),
        REFUSE("Refusé", "#EF4444"),
        EXPIRE("Expiré", "#6B7280");
        
        private final String displayName;
        private final String color;
        
        StatutDevis(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    private Long id;
    private String numero;
    private LocalDate dateCreation;
    private LocalDate dateValidite;
    private Client client;
    private StatutDevis statut;
    private Integer version;
    private List<DocumentLine> lignes = new ArrayList<>();
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    
    // Constructors
    public Devis() {
        this.statut = StatutDevis.BROUILLON;
        this.version = 1;
        this.dateCreation = LocalDate.now();
        this.montantHT = BigDecimal.ZERO;
        this.montantTVA = BigDecimal.ZERO;
        this.montantTTC = BigDecimal.ZERO;
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDate getDateValidite() { return dateValidite; }
    public void setDateValidite(LocalDate dateValidite) { this.dateValidite = dateValidite; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public StatutDevis getStatut() { return statut; }
    public void setStatut(StatutDevis statut) { this.statut = statut; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }
    
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }

    public List<DocumentLine> getLignes() { return lignes; }
    public void setLignes(List<DocumentLine> lignes) { this.lignes = lignes; }

    /**
     * Recalcule les montants à partir des lignes.
     */
    public void recalculerMontants() {
        DocumentTotalsCalculator.Totaux t = DocumentTotalsCalculator.compute(lignes);
        this.montantHT = t.totalHT;
        this.montantTVA = t.totalTVA;
        this.montantTTC = t.totalTTC;
    }
    
    @Override
    public String toString() {
        return numero + " (" + client + ")";
    }
} 
