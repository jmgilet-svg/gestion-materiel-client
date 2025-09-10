package com.materiel.client.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.materiel.client.model.DocumentType;
import com.materiel.client.service.SequenceService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation mock du service de séquences basée sur un fichier JSON.
 */
public class SequenceServiceMock implements SequenceService {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<String, Integer> sequences;

    public SequenceServiceMock(Path dataDir) {
        this.file = dataDir.resolve("sequences.json");
        load();
    }

    private void load() {
        try {
            if (Files.exists(file)) {
                sequences = mapper.readValue(file.toFile(), new TypeReference<Map<String, Integer>>(){});
            } else {
                sequences = new HashMap<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), sequences);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized String nextNumber(DocumentType type, LocalDate date) {
        String year = String.valueOf(date.getYear());
        String key = type.name() + "-" + year;
        int next = sequences.getOrDefault(key, 0) + 1;
        sequences.put(key, next);
        save();
        return prefix(type) + "-" + year + String.format("-%05d", next);
    }

    private String prefix(DocumentType type) {
        return switch (type) {
            case QUOTE -> "DEV";
            case ORDER -> "CMD";
            case DELIVERY_NOTE -> "BL";
            case INVOICE -> "FAC";
        };
    }
}
