package com.internal.utility;

import java.util.Random;

public class RandomUtil
{
    /**
     * 生成n位的随机数
     *
     * @param n
     * @return string类型的随机数
     */
    public static String generatePassword(int n) {
        Random rand = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < n; i++) {
            char c = (char) (0x30 + rand.nextInt(10));
            sb.append(c);
        }
        return sb.toString();
    }
}
