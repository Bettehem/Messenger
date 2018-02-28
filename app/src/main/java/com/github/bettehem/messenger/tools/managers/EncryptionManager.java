package com.github.bettehem.messenger.tools.managers;

//import android.util.Base64;

import org.apache.commons.codec.android.binary.Base64;

import java.security.MessageDigest;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {
    //private final int BASE64_FLAGS = Base64.URL_SAFE | Base64.NO_PADDING;
    //public static final int BASE64_FLAGS = Base64.DEFAULT;
    //public static final int BASE64_FLAGS = Base64.NO_WRAP | Base64.URL_SAFE | Base64.NO_PADDING;

    public static String createHash(String secretText){
        try{

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(secretText.getBytes("UTF-8"));
            /*sha.update(stringForEncryption.getBytes("UTF-8"));
            byte[] keyBytes = new byte[16];
            System.arraycopy(sha.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            IvParameterSpec spec = new IvParameterSpec(scramble(stringForEncryption).getBytes("UTF-8"));
            byte[] key = new byte[32];
            System.arraycopy(spec.getIV(), 0, key, 0, 16);
            System.arraycopy(secretKeySpec.getEncoded(), 0, key, 16, 16);
            return Base64.encodeBase64String(new SecretKeySpec(key, "AES").getEncoded());


            byte[] keyBytes = new byte[16];


            return Base64.encodeBase64String(new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES").getEncoded());
            */
            //return Base64.encodeToString(hash, BASE64_FLAGS);
            return Base64.encodeBase64URLSafeString(hash);
        }catch (Exception e){
            e.printStackTrace();
            return "null";
        }
    }

    /**
     *
     * @param key secret key
     * @param text text for encryption
     * @return returns an ArrayList with index 0 = iv, and 1 = hash
     */
    public static ArrayList<String> encrypt(String key, String text){
        try
        {
            //byte[] encodedKey = Base64.decode(key, BASE64_FLAGS);
            byte[] encodedKey = Base64.decodeBase64(key);

            /*
            byte[] ivBytes = new byte[16];
            System.arraycopy(encodedKey, 0, ivBytes, 0, ivBytes.length);
            IvParameterSpec spec = new IvParameterSpec(ivBytes);
            */

            //byte[] keyBytes = new byte[16];
            //System.arraycopy(encodedKey, 16, keyBytes, 0, 16);
            SecretKeySpec originalKey = new SecretKeySpec(encodedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            //final String encryptedString = Base64.encodeToString(cipher.doFinal(text.getBytes("UTF-8")), BASE64_FLAGS);
            final String encryptedString = Base64.encodeBase64URLSafeString((cipher.doFinal(text.getBytes("UTF-8"))));
            ArrayList<String> data = new ArrayList<>();
            data.add(Base64.encodeBase64URLSafeString(cipher.getIV()));
            data.add(encryptedString);
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<String> encrypt(String iv, String key, String text){
        try
        {
            //byte[] encodedKey = Base64.decode(key, BASE64_FLAGS);
            byte[] encodedKey = Base64.decodeBase64(key);

            /*
            byte[] ivBytes = new byte[16];
            System.arraycopy(encodedKey, 0, ivBytes, 0, ivBytes.length);
            IvParameterSpec spec = new IvParameterSpec(ivBytes);
            */

            //byte[] keyBytes = new byte[16];
            //System.arraycopy(encodedKey, 16, keyBytes, 0, 16);
            SecretKeySpec originalKey = new SecretKeySpec(encodedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //cipher.init(Cipher.ENCRYPT_MODE, originalKey, new IvParameterSpec(Base64.decode(iv, BASE64_FLAGS)));
            cipher.init(Cipher.ENCRYPT_MODE, originalKey, new IvParameterSpec(Base64.decodeBase64(iv)));
            //final String encryptedString = Base64.encodeToString(cipher.doFinal(text.getBytes("UTF-8")), BASE64_FLAGS);
            final String encryptedString = Base64.encodeBase64URLSafeString(cipher.doFinal(text.getBytes("UTF-8")));
            ArrayList<String> data = new ArrayList<>();
            //data.add(Base64.encodeToString(cipher.getIV(), BASE64_FLAGS));
            data.add(Base64.encodeBase64URLSafeString(cipher.getIV()));
            data.add(encryptedString);
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String decrypt(String iv, String key, String text){
        try
        {
            //byte[] encodedKey = Base64.decode(key, BASE64_FLAGS);
            byte[] encodedKey = Base64.decodeBase64(key);

            //byte[] ivBytes = Base64.decode(iv, BASE64_FLAGS);
            byte[] ivBytes = Base64.decodeBase64(iv);
            //System.arraycopy(encodedKey, 0, ivBytes, 0, ivBytes.length);
            //IvParameterSpec spec = new IvParameterSpec(ivBytes);

            //byte[] keyBytes = new byte[16];
            //System.arraycopy(encodedKey, 16, keyBytes, 0, 16);
            SecretKeySpec originalKey = new SecretKeySpec(encodedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, originalKey, new IvParameterSpec(ivBytes));

            //final String decryptedString = new String(Base64.decode(cipher.doFinal(Base64.decode(text, BASE64_FLAGS)), BASE64_FLAGS));
            final String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(text.getBytes())));
            return decryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }
    }

    /*
    public static boolean test(String password, String testWord){
        String decrypted = decrypt(createHash(password), testWord);
        return decrypted.contentEquals("yes");
    }*/

    public static String scramble(String scramble){
        return new StringBuilder(scramble).reverse().toString();
    }

    public static String unscramble(String scrambledString){
        return new StringBuilder(scrambledString).reverse().toString();
    }

    public static String[] unscramble(String[] scrambledStringArray){
        String[] scrambledArray = scrambledStringArray;
        String unscrambled = "";
        for (String s : scrambledArray){
            if (unscrambled.contentEquals("") || s.contentEquals("")){
                unscrambled = new StringBuilder(s).reverse().toString();
            }else{
                unscrambled = unscrambled + "," + new StringBuilder(s).reverse().toString();
            }
        }
        String[] unscrambledArray = unscrambled.split(",");
        return unscrambledArray;
    }
}
