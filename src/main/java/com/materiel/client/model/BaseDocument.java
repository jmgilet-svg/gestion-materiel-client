package com.materiel.client.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe de base pour tous les documents commerciaux.
 */
public abstract class BaseDocument {
    private UUID id;
    private String number;
    private LocalDate date = LocalDate.now();
    private UUID customerId;
    private String customerName;
    private List<DocumentLine> lines = new ArrayList<>();
    private BigDecimal totalHT = BigDecimal.ZERO;
    private BigDecimal totalTVA = BigDecimal.ZERO;
    private BigDecimal totalTTC = BigDecimal.ZERO;
    private DocumentStatus status = DocumentStatus.DRAFT;
    private String notes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public List<DocumentLine> getLines() { return lines; }
    public void setLines(List<DocumentLine> lines) { this.lines = lines; }
    public BigDecimal getTotalHT() { return totalHT; }
    public BigDecimal getTotalTVA() { return totalTVA; }
    public BigDecimal getTotalTTC() { return totalTTC; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Recalcule les totaux à partir des lignes.
     */
    public void recalcTotals() {
        totalHT = lines.stream().map(DocumentLine::getTotalHT)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalTVA = lines.stream().map(DocumentLine::getTotalTVA)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalTTC = totalHT.add(totalTVA);
    }
}
