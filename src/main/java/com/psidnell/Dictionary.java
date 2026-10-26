package com.psidnell;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dictionary {
    private Set<String> words;

    public Dictionary() throws IOException, URISyntaxException {
        this.words = new HashSet<>();
        loadDictionary();
    }

    private void loadDictionary() throws IOException, URISyntaxException {
        // Load the JSON file
        URL resource = getClass().getResource("/com/psidnell/Dictionary.json");
        if (resource == null) {
            throw new IOException("Dictionary.json not found in classpath");
        }

        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);

        // Extract words from the JSON structure
        JsonNode wordsNode = rootNode.get("words");
        if (wordsNode.isArray()) {
            for (JsonNode wordNode : wordsNode) {
                if (wordNode.isTextual()) {
                    words.add(wordNode.asText().toLowerCase());
                }
            }
        }
    }

    public Set<String> getWords() {
        return words;
    }

    public boolean contains(String word) {
        return words.contains(word.toLowerCase());
    }
}
