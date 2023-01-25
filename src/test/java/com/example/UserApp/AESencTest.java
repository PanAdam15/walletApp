package com.example.UserApp;

import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;

import static com.example.UserApp.AESenc.generateKey;
import static org.testng.Assert.*;

public class AESencTest {
    AESenc aeSenc;
    Key generatedKey;
    String textToEncrypt = "Ala ma kota";
    String textEncrypted = null;

    private static final String Algo = "AES";
    private static final byte[] keyValue
            = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
    @org.testng.annotations.BeforeMethod
    public void setUp() {
        aeSenc = new AESenc();
        generatedKey = generateKey(keyValue, Algo);
    }

    @org.testng.annotations.Test
    public void encryptedTextIsDifferentThanOriginal() throws Exception {
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generateKey(keyValue, Algo));
        assertNotEquals(textEncrypted,textToEncrypt);
    }

    @org.testng.annotations.Test
    public void decryptedTextIsSameAsOriginal() throws Exception {
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generatedKey);
        assertEquals(textDecrypted,textToEncrypt);
    }

    @org.testng.annotations.Test
    public void decryptedTextIsDifferentThanEncryptedText() throws Exception {
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generatedKey);
        assertNotEquals(textDecrypted,textEncrypted);
    }

    @org.testng.annotations.Test(expectedExceptions = BadPaddingException.class)
    public void decryptedTextWithNewKeyDoesNotMatchOriginal() throws Exception {
        byte[] newKeyValue = new byte[]{'T', 'f', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generateKey(newKeyValue,Algo));
        assertNotEquals(textDecrypted,textToEncrypt);
    }

    @org.testng.annotations.Test(expectedExceptions = InvalidKeyException.class)
    public void decryptedTextWithInvalidAlgorithmDoesNotMatchOriginal() throws Exception {
        String newAlgo = "DES";
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generateKey(keyValue,newAlgo));
        assertNotEquals(textDecrypted,textToEncrypt);
    }

}