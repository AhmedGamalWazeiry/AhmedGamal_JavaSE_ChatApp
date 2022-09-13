package client_chatapp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class StatisticsData extends DataPreparation {

    private File directory;
    private File file;

    private RandomAccessFile accessToStatisticsFile;

    private String username;

    // initialize directory and statistics file, every conversation has its own
    // statistics file with a number

    public StatisticsData(String username) {

        this.username = username;

        int fileNumber = 0;
        directory = new File("Statistics\\" + username);

        while (true) {

            fileNumber++;

            if (!directory.exists()) {

                directory.mkdirs();
            }

            file = new File(directory + "\\Statistics_Conversation" + "_" + fileNumber + ".txt");

            if (!file.exists())
                break;

        }
        try {
            accessToStatisticsFile = new RandomAccessFile(
                    "Statistics\\" + username + "\\Statistics_Conversation" + "_" + fileNumber + ".txt", "rw");
        } catch (IOException e) {
        }
    }

    // the last total statistics file will be read to merge with the preparing words
    // for this conversation, so we need to read the last statistics file only

    public void generateTotalStatisticsFile() {

        directory = new File("Statistics\\" + username + "\\Total_Statistics");

        if (!directory.exists())
            directory.mkdirs();

        file = new File(directory + "\\Statistics_TotalConversations.txt");
        try {
            accessToStatisticsFile = new RandomAccessFile(
                    file, "rw");

        } catch (IOException e) {

        }

        String readData = "", word;
        int numberOfOccurrences;
        try {
            while (readData != null) {

                readData = accessToStatisticsFile.readLine();
                if (readData != null) {

                    String data[] = readData.split(" ");

                    word = data[0];
                    numberOfOccurrences = Integer.parseInt(data[1]);

                    if (words.containsKey(word)) {
                        words.put(word, (words.get(word) + numberOfOccurrences));
                    } else {
                        words.put(word, numberOfOccurrences);
                    }

                }

            }
            accessToStatisticsFile.seek(0);
        } catch (IOException e) {

        }

        generateStatisticsFile();

    }

    // the preparing words which it stored in hashMap words in data preparation
    // class will be sorted first and written into its own file

    // small note, generateStatisticsFile called by generateTotalStatisticsFile also
    // but after merge.

    public void generateStatisticsFile() {

        HashMap<String, Integer> newWords = sortWordsByValue();

        for (Map.Entry<String, Integer> word : newWords.entrySet()) {
            try {
                accessToStatisticsFile.writeBytes(word.getKey() + " " + word.getValue() + "\r\n");
            } catch (IOException e) {

            }
        }

    }

}
