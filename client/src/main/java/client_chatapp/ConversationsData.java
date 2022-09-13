package client_chatapp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ConversationsData {
    private File directory;
    private File file;
    private RandomAccessFile accessToConversationFile;

    // initialize directory and conversion file

    public ConversationsData(String username) {

        directory = new File("Conversations\\" + username);

        if (!directory.exists()) {
            directory.mkdirs();
            file = new File(directory + "\\Conversation.txt");
        }

        try {
            accessToConversationFile = new RandomAccessFile("Conversations\\" + username + "\\Conversation.txt", "rw");
        } catch (IOException e) {
        }

    }

    // add the messages to the conversation file

    public void addMessage(String message) {

        try {

            long fileLength = accessToConversationFile.length();
            accessToConversationFile.seek(fileLength);

            accessToConversationFile.writeBytes(message + "\r\n");

        } catch (IOException e) {
        }
    }

}