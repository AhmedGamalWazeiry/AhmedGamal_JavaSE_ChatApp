package server_chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // if the client connects to the server, the server accepts him and it will
    // create a new thread for this client

    public void startServer() throws NoSuchAlgorithmException, InvalidKeySpecException {

        try {
            System.out.println("Waiting for clients to connect..........");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        ServerSocket serverSocket = new ServerSocket(8888);

        Server server = new Server(serverSocket);
        server.startServer();

    }
}