package com.materiel.client.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stockage générique JSON d'une liste d'objets.
 */
public class JsonStore<T> {
    private final ObjectMapper mapper;
    private final Path file;
    private final Class<T[]> arrayType;

    public JsonStore(Path file, Class<T[]> arrayType) {
        this.file = file;
        this.arrayType = arrayType;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public synchronized List<T> load() {
        try {
            if (Files.notExists(file)) {
                return new ArrayList<>();
            }
            T[] arr = mapper.readValue(file.toFile(), arrayType);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture JSON", e);
        }
    }

    public synchronized void save(List<T> data) {
        try {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Erreur d'écriture JSON", e);
        }
    }
}
