package com.psidnell;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dictionary implements Component {
    private  Map<String, Word> words;

    @Override
    public void setContext(Context context) throws Exception {
        Component.super.setContext(context);
        words = loadDictionary();
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

        Map<String, Word> words = new HashMap<>();

        // Extract words from the JSON structure
        JsonNode wordsNode = rootNode.get("words");
        if (wordsNode.isArray()) {
            for (JsonNode wordNode : wordsNode) {
                if (wordNode.isTextual()) {
                    String wordText = wordNode.asText().toLowerCase();
                    Word word = new Word(wordText);
                    words.put(wordText, word);
                }
            }
        }

        return Collections.unmodifiableMap(words);
    }

    public Map<String, Word> getWords() {
        return words;
    }

    public boolean isWord(String word) {
        return words.keySet().contains(word);
    }
}
