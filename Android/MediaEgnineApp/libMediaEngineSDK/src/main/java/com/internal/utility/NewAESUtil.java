package com.internal.utility;

import java.io.InputStream;

public class NewAESUtil
{
    /**
     * 加密方法
     *
     * @param key
     *            密钥长度
     * @param iv
     *            IV数组
     * @param data
     *            加密的字符串
     * @return 加密之后的字符串
     */
    public static String encryptDatas(byte[] key, String iv, String data) {
        byte[] en = null;
        try {
            NewAES aes = new NewAES(key, iv.getBytes());
            en = aes.encrypt(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NewAES.toHexString(en);
    }

    public static byte[] encryptDatas(byte[] key, String iv, InputStream in) {
        try {
            NewAES aes = new NewAES(key, iv.getBytes());
            return aes.encrypt(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptDatasUseBase64(byte[] key, String iv, String data) {
        try {
            NewAES aes = new NewAES(key, iv.getBytes());
            return aes.encrypt(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密方法
     *
     * @param key
     *            密钥长度
     * @param iv
     *            IV数组
     * @param data
     *            解密的字符串
     * @return 解密之后的字符串
     */
    public static String decryptDatas(byte[] key, String iv, String data) {
        byte[] de = null;
        try {
            NewAES aes = new NewAES(key, iv.getBytes());
            de = aes.decrypt(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(de);
    }

    public static String decryptDatas(byte[] key, String iv, byte[] data) {
        byte[] de = null;
        try {
            NewAES aes = new NewAES(key, iv.getBytes());
            de = aes.decrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(de);
    }
}
