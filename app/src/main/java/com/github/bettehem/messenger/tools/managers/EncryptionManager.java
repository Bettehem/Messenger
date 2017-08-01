package com.github.bettehem.messenger.tools.managers;



import org.apache.commons.codec.android.binary.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {

    public static String createKey(String stringForEncryption){
        try{
            char[] array = new char[stringForEncryption.length()];
            stringForEncryption.getChars(0, stringForEncryption.length(), array, 0);

            String rev = new StringBuilder(stringForEncryption).reverse().toString();
            byte[] salt = rev.getBytes("UTF-8");
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            //KeySpec spec = new PBEKeySpec(array);
            KeySpec spec = new PBEKeySpec(array, salt, 65536, 2048);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final String encryptedString = Base64.encodeBase64String(cipher.doFinal(stringForEncryption.getBytes()));
            //return new String[]{encryptedString, Base64.encodeBase64String(key.getEncoded())};
            return encryptedString;
        }catch (Exception e){
            e.printStackTrace();
            return "null";
        }
    }

    public static String encrypt(String key, String text){
        try
        {
            byte[] encodedKey = Base64.decodeBase64(key);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            final String encryptedString = Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
            return encryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }
    }

    public static String decrypt(String key, String text){
        try
        {
            byte[] encodedKey = Base64.decodeBase64(key);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            final String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(text)));
            return decryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }
    }

    public static boolean test(String password, String testWord){
        String decrypted = decrypt(createKey(password), testWord);
        return decrypted.contentEquals("yes");
    }

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
