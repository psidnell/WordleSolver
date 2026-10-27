package com.psidnell;

import java.util.*;

public class Word {
    private final String word;

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public int occurencesOf(Character c) {
        int occurrences = 0;
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == c) {
                occurrences++;
            }
        }
        return occurrences;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            return ((Word) obj).word.equals(word);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return word;
    }

    public boolean haveCommonCharacters(Word other) {
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            if (other.occurencesOf(c) > 0) {
                return true;
            }
        }
        return false;
    }



    public Map<Character, Integer> charactersInCommon(Word other) {
        Map<Character, Integer> result = new TreeMap<>();
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            int occurrences = other.occurencesOf(c);
            if (occurrences > 0) {
                result.put(c, occurrences);
            }
        }
        return result;
    }

//    public Map<Character, Integer> charactersInCommon(Word other) {
//        Map<Character, Integer> result = new TreeMap<>();
//        for (int i = 0; i < 5; i++) {
//            Character c1 = word.charAt(i);
//            Character c2 = word.charAt(i);
//            if (c1.equals(c2)) {
//                if (result.containsKey(c1)) {
//                    result.put(c1, result.get(c1) + 1);
//                } else {
//                    result.put(c1, 1);
//                }
//            }
//        }
//        return result;
//    }

    public String charactersMissing(Word other) {
        StringBuilder result = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Character c = word.charAt(i);
            if (other.occurencesOf(c) == 0 && !seen.contains(c)) {
                result.append(c);
                seen.add(c);
            }
        }
        return result.toString();
    }

    public WordComparison compareWords(Word other) {
        Boolean[] correctPositions = new Boolean[5];
        Boolean[] incorrectPositions = new Boolean[5];
        Boolean[] mismatches = new Boolean[5];

        for (int i = 0; i < 5; i++) {
            Character c1 = word.charAt(i);
            Character c2 = other.word.charAt(i);

            if (c1.equals(c2)) {
                correctPositions[i] = true;
                incorrectPositions[i] = false;
                mismatches[i] = false;
            } else if (other.occurencesOf(c1) > 0){
                correctPositions[i] = false;
                incorrectPositions[i] = true;
                mismatches[i] = false;
            } else {
                correctPositions[i] = false;
                incorrectPositions[i] = false;
                mismatches[i] = true;
            }
        }


        String charactersMissing = charactersMissing(other);
        Map<Character, Integer> commonFrequencies =  charactersInCommon(other);
        return new WordComparison(commonFrequencies, charactersMissing, equals(other), correctPositions, incorrectPositions, mismatches);
    }

    public Character charAt(int i) {
        return word.charAt(i);
    }
}
