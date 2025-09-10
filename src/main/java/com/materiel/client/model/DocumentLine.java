package com.materiel.client.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ligne d'un document (devis, commande, etc.).
 */
public class DocumentLine {
    private UUID id = UUID.randomUUID();
    private String designation; // désignation/description
    private String unite;       // "h", "jour", "pièce", etc.
    private BigDecimal quantite = BigDecimal.ONE;
    private BigDecimal prixUnitaireHT = BigDecimal.ZERO;
    private BigDecimal remisePct = BigDecimal.ZERO;   // 0..100
    private BigDecimal tvaPct = new BigDecimal("20.0");

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) { this.prixUnitaireHT = prixUnitaireHT; }
    public BigDecimal getRemisePct() { return remisePct; }
    public void setRemisePct(BigDecimal remisePct) { this.remisePct = remisePct; }
    public BigDecimal getTvaPct() { return tvaPct; }
    public void setTvaPct(BigDecimal tvaPct) { this.tvaPct = tvaPct; }
}
