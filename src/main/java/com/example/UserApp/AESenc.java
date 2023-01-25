package com.example.UserApp;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Random;

public class AESenc {
 private static final Random RANDOM = new SecureRandom();
 private static final String HMAC_SHA512 = "HmacSHA512";
 private static final String Algo = "AES";
 private static final byte[] keyValue
         = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

 //encrypts string and returns encrypted string
 public String encrypt(String data, Key key) throws Exception {
  Cipher c = Cipher.getInstance(Algo);
  c.init(Cipher.ENCRYPT_MODE, key);
  byte[] encVal = c.doFinal(data.getBytes());
  return Base64.getEncoder().encodeToString(encVal);
 }

 //decrypts string and returns plain text
 public String decrypt(String encryptedData, Key key) throws Exception {
  Cipher c = Cipher.getInstance(Algo);
  c.init(Cipher.DECRYPT_MODE, key);
  byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
  byte[] decValue = c.doFinal(decodedValue);
  return new String(decValue);
 }

 public static byte[] getNextSalt() {
  byte[] salt = new byte[16];
  RANDOM.nextBytes(salt);
  return salt;
 }

 // Generate a new encryption key.
   public Key generateKey(byte[] keyValue, String Algo) {
  return new SecretKeySpec(keyValue, Algo);
 }

 public Key generateKey() {
  return new SecretKeySpec(keyValue, Algo);
 }

 public String calculateHMAC(String text, byte[] key){
  Mac sha512Hmac;
  String result="";
  try {
   sha512Hmac = Mac.getInstance(HMAC_SHA512);
   SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_SHA512);
   sha512Hmac.init(keySpec);
   byte[] macData = sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));
   result = Base64.getEncoder().encodeToString(macData);
  } catch (InvalidKeyException | NoSuchAlgorithmException e) {
   e.printStackTrace();
  } finally {
  }
  return result;
 }

 public String calculateSHA512(String text) {
  try {
   //get an instance of SHA-512
   MessageDigest md = MessageDigest.getInstance("SHA-512");

   //calculate message digest of the input string - returns byte array
   byte[] messageDigest = md.digest(text.getBytes());

   // Convert byte array into signum representation
   BigInteger no = new BigInteger(1, messageDigest);

   // Convert message digest into hex value
   String hashtext = no.toString(16);

   // Add preceding 0s to make it 32 bit
   while (hashtext.length() < 32) {
    hashtext = "0" + hashtext;
   }

   // return the HashText
   return hashtext;
  }

  // If wrong message digest algorithm was specified
  catch (NoSuchAlgorithmException e) {
   throw new RuntimeException(e);

  }
 }
}