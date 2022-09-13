package server_chatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public Authenticator authenticator = new Authenticator();
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String clientPassword;

    private Boolean isAuthorized = false;

    // once the client connects to the server, the authentication process will start
    // to authenticate the client to enter the group chat, and add this client to
    // the list where all online clients there

    public ClientHandler(Socket socket) throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    // the server will be sent a broadcast message if any client sent to it a
    // message
    public void run() {
        try {
            authenticationProcess();
        } catch (NoSuchAlgorithmException e) {

            closeEverything(socket, bufferedWriter, bufferedReader);
        } catch (InvalidKeySpecException e) {

            closeEverything(socket, bufferedWriter, bufferedReader);
        }

        if (isAuthorized) {
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!", true);
        }
        String messageFromClient;

        while (!socket.isClosed()) {

            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null)
                    broadcastMessage(messageFromClient, false);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }

        }

    }

    // if the client is connected to the server, the server asks him to sign in or
    // sign up , if signing in the server will authenticate this client with the
    // data in the file, and if signing up the server directly stores this client to
    // file.
    private void authenticationProcess() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String userStatus = "-1";

        try {

            while (socket.isConnected()) {

                this.bufferedWriter.write("Sign In Enter 1 , Sign Up Enter 2");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();

                userStatus = bufferedReader.readLine();

                this.bufferedWriter.write("Please Enter your username");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();

                this.clientUsername = bufferedReader.readLine();

                this.bufferedWriter.write("Please Enter your Password");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();

                this.clientPassword = bufferedReader.readLine();

                boolean isMatched = authenticator.authenticateClient(this.clientUsername, this.clientPassword);

                if (userStatus.equals("2")) {
                    if (!authenticator.db.isUsernameUsed(clientUsername)) {
                        authenticator.addClient(this.clientUsername, this.clientPassword);
                        break;
                    }
                    this.bufferedWriter.write("somone using this username or password, to try again press enter.");
                    this.bufferedWriter.newLine();
                    this.bufferedWriter.flush();
                    bufferedReader.readLine();

                } else if (userStatus.equals("1")) {
                    if (isMatched) {
                        break;
                    } else {
                        this.bufferedWriter.write("Sign in failed, to try again press enter.");
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                        bufferedReader.readLine();
                    }
                }
            }
            isAuthorized = true;
            this.bufferedWriter.write("You successfully logged in !");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    // sent messages to all client in the list.

    private void broadcastMessage(String messageToSend, Boolean isFromServer) {

        for (ClientHandler clientHandler : clientHandlers) {
            try {

                if (isFromServer) {
                    clientHandler.bufferedWriter.write(messageToSend);
                } else if (!clientHandler.equals(this)) {
                    clientHandler.bufferedWriter.write((clientUsername + ": " + messageToSend));

                } else {

                    clientHandler.bufferedWriter.write(("You: " + messageToSend));
                }

                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }

        if (messageToSend.equals("Bye Bye")) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    // if any client sent "Bye Bye" the server will close this socket and removes
    // this client from the list

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!", true);
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        if (isAuthorized)
            removeClientHandler();
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
            e.printStackTrace();
        }
    }

}
