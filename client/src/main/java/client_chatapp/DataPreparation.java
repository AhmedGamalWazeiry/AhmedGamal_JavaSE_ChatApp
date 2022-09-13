package client_chatapp;

import java.util.*;

public class DataPreparation {

    public HashMap<String, Integer> words = new HashMap<String, Integer>();

    // extract words from message and append to words HasMap

    public void prepareWords(String message) {

        String word = "";

        for (int i = 0; i < message.length(); i++) {
            if (Character.isWhitespace(message.charAt(i))) {

                if (!word.isEmpty()) {
                    if (words.containsKey(word))
                        words.put(word, words.get(word) + 1);
                    else
                        words.put(word, 1);
                }

                word = "";
                continue;
            }
            word += message.charAt(i);
        }
        if (!word.isEmpty()) {
            if (words.containsKey(word))
                words.put(word, (words.get(word) + 1));
            else
                words.put(word, 1);
        }
    }

    protected HashMap<String, Integer> sortWordsByValue() {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(words.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> newWords = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            newWords.put(aa.getKey(), aa.getValue());
        }
        return newWords;
    }

}
