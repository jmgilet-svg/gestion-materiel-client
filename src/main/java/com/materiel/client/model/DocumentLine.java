package com.materiel.client.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Ligne d'un document (devis, commande, etc.).
 */
public class DocumentLine {
    private UUID productId;
    private String label;
    private BigDecimal quantity = BigDecimal.ZERO;
    private String unit;
    private BigDecimal unitPriceHT = BigDecimal.ZERO;
    private BigDecimal discountPct = BigDecimal.ZERO; // pourcentage 0-100
    private BigDecimal vatRate = BigDecimal.ZERO; // pourcentage 0-100

    public DocumentLine() {
    }

    public DocumentLine(UUID productId, String label, BigDecimal quantity, String unit,
                        BigDecimal unitPriceHT, BigDecimal discountPct, BigDecimal vatRate) {
        this.productId = productId;
        this.label = label;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPriceHT = unitPriceHT;
        this.discountPct = discountPct;
        this.vatRate = vatRate;
    }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getUnitPriceHT() { return unitPriceHT; }
    public void setUnitPriceHT(BigDecimal unitPriceHT) { this.unitPriceHT = unitPriceHT; }
    public BigDecimal getDiscountPct() { return discountPct; }
    public void setDiscountPct(BigDecimal discountPct) { this.discountPct = discountPct; }
    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    /**
     * Total HT de la ligne = qty * prix * (1 - remise).
     */
    public BigDecimal getTotalHT() {
        BigDecimal base = unitPriceHT.multiply(quantity);
        BigDecimal discount = base.multiply(discountPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return base.subtract(discount);
    }

    /**
     * TVA de la ligne.
     */
    public BigDecimal getTotalTVA() {
        return getTotalHT().multiply(vatRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
