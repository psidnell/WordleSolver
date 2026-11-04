package com.psidnell.words;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psidnell.util.Component;
import com.psidnell.util.Context;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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

    private Map<String, Word> loadDictionary() throws IOException, URISyntaxException {
        // Load the JSON file
        URL resource = getClass().getResource("/com/psidnell/Dictionary.json");
        if (resource == null) {
            throw new IOException("Dictionary.json not found in classpath");
        }

        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);

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

    public Collection<Word> getWords() {
        return words.values();
    }

    public boolean hasWord(String word) {
        return words.containsKey(word.toUpperCase());
    }
}
