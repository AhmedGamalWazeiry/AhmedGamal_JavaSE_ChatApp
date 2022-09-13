package server_chatapp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DataBaseManager {

    private File directory;
    private File file;
    private RandomAccessFile accessFile;

    // initialize databse directory and Clients file

    public DataBaseManager() {

        directory = new File("database");
        if (!directory.exists()) {
            directory.mkdirs();
            file = new File(directory + "\\Clients.txt");

        }
        try {
            accessFile = new RandomAccessFile("database\\Clients.txt", "rw");
        } catch (IOException e) {
        }
    }

    // store the client directly in the clients file

    public void addClient(String username, String hashedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {

            long fileLength = accessFile.length();
            accessFile.seek(fileLength);

            accessFile.writeBytes("Username : " + username + "\r\n");
            accessFile.writeBytes("Password : " + hashedPassword + "\r\n");

        } catch (IOException e) {
        }

    }

    // get password by username from clients file to start validation in
    // Authenticator class
    public String getClientPassword(String username)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {

            accessFile.seek(0);
            String readData = "", usernameInFile = "", hasedPasswordInFile = "";

            while (readData != null) {

                readData = accessFile.readLine();
                if (readData != null)
                    usernameInFile = readData.substring(11);

                readData = accessFile.readLine();

                if (readData != null)
                    hasedPasswordInFile = readData.substring(11);

                if (username.equals(usernameInFile)) {

                    return hasedPasswordInFile;
                }

            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public Boolean isUsernameUsed(String username)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {

            accessFile.seek(0);
            String readData = "", usernameInFile = "";

            while (readData != null) {

                readData = accessFile.readLine();
                if (readData != null)
                    usernameInFile = readData.substring(11);

                readData = accessFile.readLine();

                if (username.equals(usernameInFile)) {
                    return true;
                }

            }
        } catch (IOException e) {
        }
        return false;
    }
}
