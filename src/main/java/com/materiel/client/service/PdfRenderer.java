package com.materiel.client.service;

import com.materiel.client.model.DocumentType;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Interface de rendu PDF. Implémentation minimale pour les tests.
 */
public interface PdfRenderer {
    Path render(DocumentType type, UUID id);
}
