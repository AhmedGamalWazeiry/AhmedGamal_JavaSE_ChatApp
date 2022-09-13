package server_chatapp;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Authenticator extends Hasher {
    public DataBaseManager db;

    public Authenticator() {
        db = new DataBaseManager();
    }

    // sending the client to the database manager to store him after hashing the
    // password
    public void addClient(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        db.addClient(username, getHashedPassword(password));
    }

    // match the client to stored data

    public Boolean authenticateClient(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hashedPassword = db.getClientPassword(username);

        if (hashedPassword == null)
            return false;

        if (validatePassword(password, hashedPassword)) {
            return true;
        }
        return false;

    }

}
