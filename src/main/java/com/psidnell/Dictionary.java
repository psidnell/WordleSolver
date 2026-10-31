package com.psidnell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psidnell.rules.RuleSet;
import com.psidnell.util.Component;
import com.psidnell.util.Context;

// Dictionary and Solutions are both wodlists
public class Dictionary implements Component {
    private Map<String, Word> words = null;
    private Set<Word> wordsRanked = null;

    public Dictionary() {
    }

    public Dictionary(String[] testWords) {
        words = new TreeMap<>();
        Arrays.stream(testWords).forEach(word -> words.put(word, new Word(word)));
        wordsRanked = calculateRanks();
    }

    @Override
    public void setContext(Context context) throws Exception {
        Component.super.setContext(context);
        if (words == null) {
            words = loadDictionary();
            writeRankFileIfMissing();
            wordsRanked = loadWordsRanked();
        }
    }

    private void writeRankFileIfMissing() throws IOException {
        File file = new File("src/main/resources/com/psidnell/Rank.json");
        if (file.exists()) {
            String p = file.getAbsolutePath();
            return;
        }

        Set<Word> wordsRanked = calculateRanks();

        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(" {\n  \"ranks\": [\n");

        int i = 0;
        for (Word w : wordsRanked) {
            writer.write("    { \"word\": \"" + w.getWord() + "\", \"rank\": " + w.getRank() + "}" );
            if (i == wordsRanked.size() - 1) {
                writer.write("\n");
            } else {
                writer.write(",\n");
            }
            i++;
        }

        writer.write("  ]\n}\n");
        writer.close();
    }

    private Set<Word> calculateRanks() {
        Set<Word> wordsRanked = new TreeSet<>();
        int bestRank = 0;
        for (Word w1 : words.values()) {
            int totalRankForWord = 0;
            for(Word w2 : words.values()) {
                if (!w1.haveCommonCharacters(w2)) {
                    //totalRankForWord = worstCaseSolutionsSize;
                    continue;
                }
                AtomicInteger rank = new AtomicInteger(0);
                w1.charactersInCommon(w2).getFrequencies().values().forEach(freq -> {
                    rank.set(rank.get() + freq);
                });
                totalRankForWord += rank.get();
            };
            Word ranked = new Word(w1.getWord(), totalRankForWord);
            wordsRanked.add(ranked);
            if (totalRankForWord > bestRank) {
                bestRank = totalRankForWord;
                System.out.println("Best so far " + ranked.getWord() + " " + ranked.getRank());
                //System.out.println(wordsRanked.size() + "/" + words.size());
            }
        }
        return wordsRanked;
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
                    String wordText = wordNode.asText().toLowerCase();
                    Word word = new Word(wordText);
                    words.put(wordText, word);
                }
            }
        }

        return Collections.unmodifiableMap(words);
    }

    private Set<Word> loadWordsRanked() throws IOException, URISyntaxException {
        // Load the JSON file
        URL resource = getClass().getResource("/com/psidnell/Rank.json");
        if (resource == null) {
            throw new IOException("Rank.json not found in classpath");
        }

        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);

        Set<Word> words = new TreeSet<>();

        // Extract words from the JSON structure
        JsonNode ranksNode = rootNode.get("ranks");
        if (ranksNode.isArray()) {
            for (JsonNode rankNode : ranksNode) {
                JsonNode wordNode = rankNode.findValue("word");
                if (wordNode.isTextual()) {
                    String wordText = wordNode.asText().toLowerCase();

                    JsonNode valNode = rankNode.findValue("rank");
                    if (valNode.isNumber()) {
                        int number = valNode.asInt();
                        Word word = new Word(wordText, number);
                        words.add(word);
                    }
                }
            }
        }

        return Collections.unmodifiableSet(words);
    }

    public Map<String, Word> getWords() {
        return words;
    }

    public boolean hasWord(String word) {
        return words.keySet().contains(word);
    }
}
