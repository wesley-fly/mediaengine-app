package com.internal.utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NewAES
{
    /**
     * CBC: 每 16Byte 为一组，加密时需要考虑上一组加密结果。需要一个初始值 IV PKCS5Padding:
     * 填充算法。填充的值是，需要填充的长度。
     */
    private static final String CBC_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private String m_transformation = null;

    /**
     * AES 加密用的密钥
     */
    private byte[] m_secretKey = new byte[16];

    /**
     * IV，CBC 模式下用
     */
    private byte[] m_iv = new byte[16];

    /**
     * 用来做加密用的 cipher 对象
     */
    private Cipher m_encryptCipher = null;

    /**
     * 用来做解密用的 cipher 对象
     */
    private Cipher m_decryptCipher = null;

    /**
     * 初始化 AES。
     *
     * @param key
     *            密钥长度必须为 16 bytes
     * @param iv
     *            IV数组，当mode=CBC时使用。mode=ECB时，此参数为null
     */
    public NewAES(byte[] key, byte[] iv) {
        if (key == null || key.length != 16)
            throw new RuntimeException("Secret key must be 16 bytes");
        if (iv == null || iv.length != 16)
            throw new RuntimeException("IV must be 16 bytes");

        System.arraycopy(key, 0, m_secretKey, 0, 16);
        System.arraycopy(iv, 0, m_iv, 0, 16);
        m_transformation = CBC_TRANSFORMATION;

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(m_secretKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(m_iv);

            m_encryptCipher = Cipher.getInstance(m_transformation);
            m_decryptCipher = Cipher.getInstance(m_transformation);

            m_encryptCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            m_decryptCipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密二进制数据
     *
     * @param rawData
     *            待加密的数据
     * @return 加密后的结果。如果要转成十六进制字符串，则可以调用 AES.toHexString() 方法
     * @throws Exception
     */
    public byte[] encrypt(byte[] rawData) throws Exception {
        return m_encryptCipher.doFinal(rawData);
    }

    public byte[] encrypt(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (-1 != (len = in.read(buffer))) {
            if (len > 0) {
                baos.write(m_encryptCipher.update(buffer, 0, len));
            }
        }
        baos.write(m_encryptCipher.doFinal());
        return baos.toByteArray();
    }

    /**
     * 解密二进制数据
     *
     * @param encryptedData
     *            加密后的数据
     * @return 解密后的结果。
     * @throws Exception
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        return m_decryptCipher.doFinal(encryptedData);
    }

    /**
     * 得到当前 AES 的工作模式
     *
     * @return
     */
    public String getMode() {
        return m_transformation;
    }

    /**
     * 将 byte 数组变成十六进制字符串
     *
     * @param buf
     *            待转换的 byte 数组
     * @return 十六进制字符串
     */
    public static String toHexString(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Integer.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    /**
     * 将十六进制字符串转换为 byte 数组，同 toHexString 对应
     *
     * @param hexString
     * @return
     */
    public static byte[] fromHexString(String hexString) {
        if (hexString == null || (hexString.length() % 2 != 0))
            return new byte[0];
        int len = hexString.length() / 2;
        byte[] ret = new byte[len];

        for (int i = 0; i < len; i++) {
            int c1 = char2int(hexString.charAt(i * 2));
            int c2 = char2int(hexString.charAt(i * 2 + 1));
            if (c1 != -1 && c2 != -1) {
                int ii = c1 * 16 + c2;
                ret[i] = (byte) ii;
            } else
                return null;
        }
        return ret;
    }

    private static int char2int(char c) {
        if (c >= '0' && c <= '9')
            return c - '0';
        else if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        else if (c >= 'a' && c <= 'f')
            return c - 'a' + 10;
        return -1;
    }
}
