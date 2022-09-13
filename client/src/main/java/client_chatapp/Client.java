package client_chatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    Scanner scanner = new Scanner(System.in);

    private Socket socket;

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private StatisticsData statisticsData;

    private String username;

    private Boolean isFileWritten = false;
    private Boolean isAuthorized = false;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    // sign-in process, the client is not signed in until the server authenticates
    // him, if the server authenticates him, it sends a "You successfully logged in
    // !"
    private Boolean signIn() {

        String messageFromServer = "", messageToSend;
        try {
            while (socket.isConnected()) {

                messageFromServer = bufferedReader.readLine();

                System.out.println(messageFromServer);

                if (messageFromServer.equals("You successfully logged in !"))
                    break;

                messageToSend = scanner.nextLine();

                if (messageFromServer.contains("username")) {
                    this.username = messageToSend;
                }

                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
        if (messageFromServer.equals("You successfully logged in !")) {

            isAuthorized = true;
            statisticsData = new StatisticsData(username);

            return true;
        } else
            return false;
    }

    // here the client can send the messages until he sends "Bye Bye";

    private void sendMessage() {
        try {
            String messageToSend = "";
            while (!socket.isClosed() && !messageToSend.equals("Bye Bye")) {

                messageToSend = scanner.nextLine();

                statisticsData.prepareWords(messageToSend);

                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    // here the client can receive the messages until he receives "Bye Bye" from the
    // server

    private void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {

                String messageFromGroupChat;

                ConversationsData conversationsData = new ConversationsData(username);

                while (!socket.isClosed()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);

                        conversationsData.addMessage(messageFromGroupChat);

                        if (messageFromGroupChat.equals("You: Bye Bye")) {
                            closeEverything(socket, bufferedWriter, bufferedReader);
                            break;
                        }

                    } catch (IOException e) {
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    // if the conversation ends or any forced exits, all the messages will be stored
    // in files, and everything will be closed
    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {

        if (!isFileWritten && isAuthorized) {
            statisticsData.generateStatisticsFile();
            statisticsData.generateTotalStatisticsFile();
            isFileWritten = true;
        }

        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

        } catch (IOException e) {

            //
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 8888);
        Client client = new Client(socket);

        if (client.signIn()) {
            client.listenForMessage();
            ;
            client.sendMessage();
        }

    }
}
