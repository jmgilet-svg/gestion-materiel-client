package com.materiel.client.service;

import com.materiel.client.model.DocumentType;

import java.time.LocalDate;

/**
 * Service de numérotation des documents.
 */
public interface SequenceService {
    String nextNumber(DocumentType type, LocalDate date);
}
