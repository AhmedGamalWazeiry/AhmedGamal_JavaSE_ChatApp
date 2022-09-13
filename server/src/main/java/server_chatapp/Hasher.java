package server_chatapp;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Hasher {

    // generate salt and hash and return a string with prefix iterations and salt
    // , to be able to hash any password which I need to validate with any
    // password stored in the file with the same salt

    public String getHashedPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        int iterations = 1000;
        char[] passwordChars = password.toCharArray();
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();

        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    // parse the stored password into salt and iterarions and hash and start hash
    // the origianl password by the same way i hash the stored password

    protected boolean validatePassword(String originalPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] passwordParts = storedPassword.split(":");
        int iterations = Integer.parseInt(passwordParts[0]);

        byte[] salt = fromHex(passwordParts[1]);
        byte[] hash = fromHex(passwordParts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(),
                salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int isDiff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            isDiff |= hash[i] ^ testHash[i];
        }
        return (isDiff == 0);
    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
