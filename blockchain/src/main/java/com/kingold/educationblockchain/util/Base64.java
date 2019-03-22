package com.kingold.educationblockchain.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
Base64加密解密
 */
public class Base64 {
    /**
     * BASE64解密
     * @param key
     * @return
     * @throws Exception
     */
    public byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     * @param key
     * @return
     * @throws Exception
     */
    public String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }
}
