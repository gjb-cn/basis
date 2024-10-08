package com.yft.zbase.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encryption {
    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description:  32位小写MD5
     */
    public static String parseStrToMd5L32(String str) throws NoSuchAlgorithmException {
        String reStr = null;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(str.getBytes());
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bytes){
            int bt = b&0xff;
            if (bt < 16){
                stringBuffer.append(0);
            }
            stringBuffer.append(Integer.toHexString(bt));
        }
        reStr = stringBuffer.toString();

        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 32位大写MD5
     */
    public static String parseStrToMd5U32(String str) throws NoSuchAlgorithmException {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null){
            reStr = reStr.toUpperCase();
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 16位小写MD5
     */
    public static String parseStrToMd5U16(String str) throws NoSuchAlgorithmException {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null){
            reStr = reStr.toUpperCase().substring(8, 24);
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 16位大写MD5
     */
    public static String parseStrToMd5L16(String str) throws NoSuchAlgorithmException {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null){
            reStr = reStr.substring(8, 24);
        }
        return reStr;
    }
}
