package com.example.walletApp;

import java.security.Key;

import static com.example.walletApp.AESenc.generateKey;
import static org.testng.Assert.*;

public class AESencTest {
    AESenc aeSenc;
    Key generatedKey;
    @org.testng.annotations.BeforeMethod
    public void setUp() {
        aeSenc = new AESenc();
        generatedKey = generateKey();
    }

    @org.testng.annotations.Test
    public void encryptedTextIsDifferentThanOriginal() throws Exception {
        String textToEncrypt = "Ala ma kota";
        String textEncrypted = "";
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generateKey());
        assertNotEquals(textEncrypted,textToEncrypt);
    }

    @org.testng.annotations.Test
    public void decryptedTextIsSameAsOriginal() throws Exception {
        String textToEncrypt = "Ala ma kota";
        String textEncrypted = "";
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generatedKey);
        assertEquals(textDecrypted,textToEncrypt);
    }

    @org.testng.annotations.Test
    public void decryptedTextIsDifferentThanEncryptedText() throws Exception {
        String textToEncrypt = "Ala ma kota";
        String textEncrypted = "";
        textEncrypted =  aeSenc.encrypt(textToEncrypt,generatedKey);
        String textDecrypted = aeSenc.decrypt(textEncrypted,generatedKey);
        assertNotEquals(textDecrypted,textEncrypted);
    }

}