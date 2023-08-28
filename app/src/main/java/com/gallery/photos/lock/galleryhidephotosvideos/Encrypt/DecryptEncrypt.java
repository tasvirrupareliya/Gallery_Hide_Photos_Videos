package com.gallery.photos.lock.galleryhidephotosvideos.Encrypt;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptEncrypt {

    public static String DecryptStr(String str) {
        DecScript script = new DecScript();
        String newStr = null;
        try {
            newStr = new String(script.decrypt(str));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newStr;
    }

    public static String EncryptStr(String str1) {
        EncScript script = new EncScript();
        String newStr = null;
        try {
            newStr = EncScript.bytesToHex(script.encrypt(str1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newStr;
    }

//    --------------------------------------------------------------------------------------------------------------

    private static final byte[] keyValue =
            new byte[]{'c', 'o', 'd', 'i', 'n', 'g', 'a', 'f', 'f', 'a', 'i', 'r', 's', 'c', 'o', 'm'};


    public static String encryptKey(String str) {
        String encrypted = "";
        try {
            encrypted = encrypt(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static String decryptKey(String str) {
        String decrypted = "";
        try {
            decrypted = decrypt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static String encrypt(String cleartext)
            throws Exception {
        byte[] rawKey = getRawKey();
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String encrypted)
            throws Exception {

        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(enc);
        return new String(result);
    }

    private static byte[] getRawKey() {
        SecretKey key = new SecretKeySpec(keyValue, "AES");
        return key.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKey skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] encrypted)
            throws Exception {
        SecretKey skeySpec = new SecretKeySpec(keyValue, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    //    --------------------------------------------------------------------------------------------------------------

    // TODO: 1/17/2020 To encrypt-decrypt file
    private static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        return secretKeyFactory.generateSecret(keySpec);
    }

    private static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    private static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, yourKey);
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    public static void decodeFile(Context mContext, String encryptedFilename, String decryptedFilename) {
        try {
            SecretKey yourKey = generateKey(new char[]{'p', 'a', 's', 's'}, decryptKey(encryptionKey()).getBytes());
            byte[] decodedData = decodeFile(yourKey, getFile(mContext, encryptedFilename));
            showImage(decodedData, decryptedFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showImage(byte[] decodedData, String decodedFileName) {

        File tempImg = new File(decodedFileName);
        try {
            if (!tempImg.exists())
                tempImg.createNewFile();

            FileOutputStream fos = new FileOutputStream(tempImg);
            fos.write(decodedData);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static byte[] readFile(String encryptedFileName) {
        byte[] contents = null;

        File file = new File(encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static byte[] getFile(Context mContext, String srcFileName) throws FileNotFoundException {
        byte[] audio_data = null;
        byte[] inarry = null;
        AssetManager am = mContext.getAssets();
        try {
            InputStream is = am.open(srcFileName); // use recorded file instead of getting file from assets folder.
            int length = is.available();
            audio_data = new byte[length];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(audio_data)) != -1) {
                output.write(audio_data, 0, bytesRead);
            }
            inarry = output.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inarry;

    }

    public static void saveFile(byte[] stringToSave, String encryptedFileName) {
        SecretKey yourKey;

        try {
            String encryptedFilePath = Environment.getExternalStorageDirectory() + File.separator + encryptedFileName;
            File file = new File(encryptedFilePath);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey(new char[]{'p', 'a', 's', 's'}, decryptKey(encryptionKey()).getBytes());
            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static native String encryptionKey();

    public static native String zipencryptionkey();
}
