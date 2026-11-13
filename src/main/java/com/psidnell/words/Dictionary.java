package com.psidnell.words;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

@Component
public class Dictionary extends WordList {

    public Dictionary() throws IOException {
        loadDictionary();
    }

    public Dictionary(String[] testWords) {
        Arrays.stream(testWords).forEach(word -> {
            String upperCase = word.toUpperCase();
            words.add(new Word(upperCase));
        });
    }

    private static final Probe PROBE = Profiler.getProbe(Dictionary.class, "loadDictionary");

    private void loadDictionary() throws IOException {
        try (Probe p = PROBE.open()) {
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(this.getClass().getResourceAsStream("/com/psidnell/Dictionary.json"))));) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(reader);

                // Extract words from the JSON structure
                JsonNode wordsNode = rootNode.get("words");
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        if (wordNode.isTextual()) {
                            String wordText = wordNode.asText().toUpperCase();
                            Word word = new Word(wordText);
                            words.add(word);
                            count++;
                        }
                    }
                }
            }
            if (count != words.size()) {
                // Have we got repeated words or has something gone wrong with word uniqueness?
                throw new IllegalStateException("Mismatch between JSON word count and dictionary size");
            }
        }
    }
}
