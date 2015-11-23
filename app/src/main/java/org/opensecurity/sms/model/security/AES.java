package org.opensecurity.sms.model.security;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    // The algorithm name
    private static final String ALGORITHM = "AES";
    // A byte array containing the key
    private static byte[] keyValue;

    /**
     * Constructor
     *
     * @param key
     *              The key to encrypt and decrypt data
     */
    public AES(String key) {
        setKeyValue(key.getBytes());
    }

    /**
     * This method encrypts data with AES algorithm
     * using the key of the class
     *
     * @param data
     *              Data to encrypt
     * @return
     *              Encrypted data
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    /**
     * This method decrypts data with AES algorithm
     * using the key of the class
     *
     * @param encryptedData
     *              Data to decrypt
     * @return
     *              Decrypted data
     * @throws Exception
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     * This method generates a key with the key value
     * that can be computed with javax.crypto method
     *
     * @return
     *              The key object
     * @throws Exception
     */
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(getKeyValue(), ALGORITHM);
        return key;
    }

    /**
     * This method return the key value
     *
     * @return
     *              The key value
     */
    public static byte[] getKeyValue() {
        return keyValue;
    }

    /**
     * This method set the key value
     *
     * @param keyValue
     *              The key value
     */
    public void setKeyValue(byte[] keyValue) {
        this.keyValue = keyValue;
    }
}
