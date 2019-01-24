package com.internal.utility;

import android.util.Base64;

public class PassWordAESUtil
{
    public static final String SALT = "929b47df661d423e89f2b694e0b4a744";

    public static final String IV = "&j6948x4#0DK)Fcd";

    /**
     * 加密方法
     *
     * @param mn
     *            手机号码
     * @param data
     *            加密的字符串
     * @return 加密之后的字符串
     */
    public static String encryptDatas(String mn, String data) {
        return Base64.encodeToString(NewAESUtil.encryptDatasUseBase64(Md5Util.getMD5Bytes(mn + SALT), IV, data + SALT),
                Base64.DEFAULT);
    }

    /**
     * 解密方法
     *
     * @param mn
     *            手机号码
     *
     * @param data
     *            解密的字符串
     * @return 解密之后的字符串
     */
    public static String decryptDatas(String mn, String data) {
        return NewAESUtil.decryptDatas(Md5Util.getMD5Bytes(mn + SALT), IV,
                Base64.decode(data.getBytes(), Base64.DEFAULT));
    }
}
