package com.laotang.quickext;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


public class CryptoUtil {
    public static String encryptDES(String src, String iv, String key) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(iv,Base64.NO_WRAP));
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return Base64.encodeToString(cipher.doFinal(src.getBytes()), Base64.DEFAULT);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptDES(String src, String iv, String key){
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(iv,Base64.NO_WRAP));
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(Base64.decode(src,Base64.DEFAULT)));
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }

}
