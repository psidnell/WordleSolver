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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

// Dictionary and Solutions are both wodlists
public class Dictionary implements Component {
    private Map<String, Word> words = null;

    public Dictionary() {
    }

    public Dictionary(String[] testWords) {
        words = new TreeMap<>();
        Arrays.stream(testWords).forEach(word -> {
            String upperCase = word.toUpperCase();
            words.put(upperCase, new Word(upperCase));
        });
    }

    @Override
    public void setContext(Context context) throws Exception {
        Component.super.setContext(context);
        if (words == null) {
            words = loadDictionary();
        }
    }

    private static final Probe PROBE = Profiler.getProbe(Dictionary.class, "loadDictionary");

    private Map<String, Word> loadDictionary() throws IOException, URISyntaxException {
        try (Probe p = PROBE.open()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(this.getClass().getResourceAsStream("/com/psidnell/Dictionary.json"))));) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(reader);

                Map<String, Word> words = new TreeMap<>();

                // Extract words from the JSON structure
                JsonNode wordsNode = rootNode.get("words");
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        if (wordNode.isTextual()) {
                            String wordText = wordNode.asText().toUpperCase();
                            Word word = new Word(wordText);
                            words.put(wordText, word);
                        }
                    }
                }
                return Collections.unmodifiableMap(words);
            }
        }
    }

    public Collection<Word> getWords() {
        return words.values();
    }

    public boolean hasWord(String word) {
        return words.containsKey(word.toUpperCase());
    }
}
