package com.example.jiangyue.androidap.util.imageload;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5 {
    private static final String algorithm = "MD5";

    // added by li xuanlin on 2013-05-17
    // 默认的密码字符串组合
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
    protected static MessageDigest messagedigest = null;
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(algorithm, e.toString());
        }
    }

    public static byte[] digest2Bytes(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception localNoSuchAlgorithmException) {
            Log.e(algorithm, localNoSuchAlgorithmException.toString());
        }
        return md.digest(bytes);
    }

    public static String digest2Str(byte[] bytes) {
        return CByte.bytes2Hex(digest2Bytes(bytes));
    }

    public static String digest2Str(String str) {
        return digest2Str(str.getBytes());
    }

    // added by li xuanlin on 2013-05-17

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static String getFileMD5String(File file) throws IOException {

        InputStream fis;
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return bufferToHex(messagedigest.digest());
    }
    // end
}
