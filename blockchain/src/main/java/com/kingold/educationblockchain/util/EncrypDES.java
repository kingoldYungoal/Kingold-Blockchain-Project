package com.kingold.educationblockchain.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncrypDES {

    //KeyGenerator 提供对称密钥生成器的功能，支持各种算法
    private KeyGenerator mKeygen;
    //SecretKey 负责保存对称密钥
    private SecretKey mDeskey;
    //Cipher负责完成加密或解密工作
    private Cipher mCipher;
    //该字节数组负责保存加密的结果
    private byte[] mCipherByte;

    public EncrypDES() throws NoSuchAlgorithmException, NoSuchPaddingException{
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        //实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
        mKeygen = KeyGenerator.getInstance("DES");
        //生成密钥
        mDeskey = mKeygen.generateKey();
        //生成Cipher对象,指定其支持的DES算法
        mCipher = Cipher.getInstance("DES");
    }

    /**
     * 对字符串加密
     *
     * @param str
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] Encrytor(String str) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
        mCipher.init(Cipher.ENCRYPT_MODE, mDeskey);
        byte[] src = str.getBytes();
        // 加密，结果保存进cipherByte
        mCipherByte = mCipher.doFinal(src);
        return mCipherByte;
    }

    /**
     * 对字符串解密
     *
     * @param buff
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] Decryptor(byte[] buff) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
        mCipher.init(Cipher.DECRYPT_MODE, mDeskey);
        mCipherByte = mCipher.doFinal(buff);
        return mCipherByte;
    }

    /**
     * @param args
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
//    public static void main(String[] args) throws Exception {
//        EncrypDES des = new EncrypDES();
//        String msg ="TEST";
//        byte[] encontent = de1.Encrytor(msg);
//        byte[] decontent = de1.Decryptor(encontent);
//        System.out.println("明文是:" + msg);
//        System.out.println("加密后:" + new String(encontent));
//        System.out.println("解密后:" + new String(decontent));
//    }
}
