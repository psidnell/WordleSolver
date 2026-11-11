package com.psidnell.words;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psidnell.profile.Probe;
import com.psidnell.profile.Profiler;
import com.psidnell.util.Component;
import com.psidnell.util.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

public class Dictionary extends WordList implements Component {

    public Dictionary() {
        super();
    }

    public Dictionary(String[] testWords) {
        super();
        Arrays.stream(testWords).forEach(word -> {
            String upperCase = word.toUpperCase();
            words.add(new Word(upperCase));
        });
    }

    @Override
    public void setContext(Context context) throws Exception {
        Component.super.setContext(context);
        if (words.isEmpty()) {
            loadDictionary();
        }
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
