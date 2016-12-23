package com.example.jiangyue.androidap.util.imageload;

import android.annotation.SuppressLint;

import java.util.Arrays;

@SuppressLint("NewApi")
public class CByte {
    private static String[] HexCode = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String byte2Hex(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1] + HexCode[d2];
    }

    public static int byte2int(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    public static int byte2int(byte[] b, int offset) {
        return b[(offset + 3)] & 0xFF | (b[(offset + 2)] & 0xFF) << 8 | (b[(offset + 1)] & 0xFF) << 16
                | (b[offset] & 0xFF) << 24;
    }

    public static long byte2long(byte[] b) {
        return b[7] & 0xFF | (b[6] & 0xFF) << 8 | (b[5] & 0xFF) << 16 | (b[4] & 0xFF) << 24 | (b[3] & 0xFF) << 32
                | (b[2] & 0xFF) << 40 | (b[1] & 0xFF) << 48 | (b[0] & 0xFF) << 56;
    }

    public static long byte2long(byte[] b, int offset) {
        return b[(offset + 7)] & 0xFF | (b[(offset + 6)] & 0xFF) << 8 | (b[(offset + 5)] & 0xFF) << 16
                | (b[(offset + 4)] & 0xFF) << 24 | (b[(offset + 3)] & 0xFF) << 32 | (b[(offset + 2)] & 0xFF) << 40
                | (b[(offset + 1)] & 0xFF) << 48 | (b[offset] & 0xFF) << 56;
    }

    public static String bytes2Hex(byte[] b) {
        char[] chars = new char[b.length * 2];
        int charsIndex = 0;

        for (int bytesIndex = 0; bytesIndex < b.length; ++bytesIndex) {
            int intValue = b[bytesIndex];
            if (intValue < 0) {
                intValue += 256;
            }

            int intValueHi = (intValue & 0xF0) >> 4;
            if (intValueHi > 9)
                chars[charsIndex] = (char) (intValueHi - 10 + 97);
            else {
                chars[charsIndex] = (char) (intValueHi + 48);
            }

            int intValueLo = intValue & 0xF;
            if (intValueLo > 9)
                chars[(charsIndex + 1)] = (char) (intValueLo - 10 + 97);
            else {
                chars[(charsIndex + 1)] = (char) (intValueLo + 48);
            }
            charsIndex += 2;
        }
        return new String(chars, 0, charsIndex);
    }

    public static String DecToOther(long iNum, int jz) throws Exception {
        long consult = 0L;
        long rest = 0L;
        StringBuffer sbBinary = new StringBuffer();
        if (iNum >= jz) {
            do {
                consult = iNum / jz;
                rest = iNum % jz;
                iNum = consult;
                if (jz == 16)
                    sbBinary.append(HexCode[(int) rest]);
                else {
                    sbBinary.append(rest);
                }
            } while (consult >= jz);
            if (jz == 16)
                sbBinary.append(HexCode[(int) consult]);
            else sbBinary.append(consult);
        } else {
            rest = (int) iNum % jz;
            if (jz == 16)
                sbBinary.append(HexCode[(int) rest]);
            else {
                sbBinary.append(rest);
            }
        }
        sbBinary.reverse();
        return sbBinary.toString();
    }

    public static byte[] hex2Bytes(String s) {
        char[] chars = s.toCharArray();
        byte[] bytes = new byte[s.length() / 2];
        int byteSize = 0;
        int charIndex = 0;
        int byteValue = 0;
        int intValue = 0;

        int position = 0;
        while (charIndex < chars.length) {
            char c = chars[(charIndex++)];
            if ((c >= '0') && (c <= '9')) {
                byteValue = c - '0';
            } else if ((c >= 'a') && (c <= 'f')) {
                byteValue = c - 'a' + 10;
            } else if ((c >= 'A') && (c <= 'F')) {
                byteValue = c - 'A' + 10;
            } else if ((c >= 65296) && (c <= 65305)) {
                byteValue = c - 65296;
            } else if ((c >= 65345) && (c <= 65350)) {
                byteValue = c - 65345 + 10;
            } else {
                if ((c < 65313) || (c > 65318))
                    continue;
                byteValue = c - 65313 + 10;
            }

            if (position == 0) {
                intValue = byteValue;
                ++position;
                bytes[byteSize] = (byte) intValue;
            } else {
                intValue = intValue * 16 + byteValue;
                position = 0;
                bytes[byteSize] = (byte) intValue;
                ++byteSize;
                if (byteSize >= bytes.length) {
                    bytes = Arrays.copyOf(bytes, bytes.length + 64);
                }
            }
        }
        return Arrays.copyOf(bytes, byteSize);
    }

    public static byte[] int2byte(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n >> 24);
        b[1] = (byte) (n >> 16);
        b[2] = (byte) (n >> 8);
        b[3] = (byte) n;
        return b;
    }

    public static void int2byte(int n, byte[] buf, int offset) {
        buf[offset] = (byte) (n >> 24);
        buf[(offset + 1)] = (byte) (n >> 16);
        buf[(offset + 2)] = (byte) (n >> 8);
        buf[(offset + 3)] = (byte) n;
    }

    public static byte[] long2byte(long n) {
        byte[] b = new byte[8];
        b[0] = (byte) (int) (n >> 56);
        b[1] = (byte) (int) (n >> 48);
        b[2] = (byte) (int) (n >> 40);
        b[3] = (byte) (int) (n >> 32);
        b[4] = (byte) (int) (n >> 24);
        b[5] = (byte) (int) (n >> 16);
        b[6] = (byte) (int) (n >> 8);
        b[7] = (byte) (int) n;
        return b;
    }

    public static void long2byte(long n, byte[] buf, int offset) {
        buf[offset] = (byte) (int) (n >> 56);
        buf[(offset + 1)] = (byte) (int) (n >> 48);
        buf[(offset + 2)] = (byte) (int) (n >> 40);
        buf[(offset + 3)] = (byte) (int) (n >> 32);
        buf[(offset + 4)] = (byte) (int) (n >> 24);
        buf[(offset + 5)] = (byte) (int) (n >> 16);
        buf[(offset + 6)] = (byte) (int) (n >> 8);
        buf[(offset + 7)] = (byte) (int) n;
    }

    public static byte[] short2byte(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n >> 8);
        b[1] = (byte) n;
        return b;
    }

    public static void short2byte(int n, byte[] buf, int offset) {
        buf[offset] = (byte) (n >> 8);
        buf[(offset + 1)] = (byte) n;
    }

    public static String ToBin(String sNum, int jz) throws Exception {
        long tempDecimal = ToDec(sNum, jz);
        return DecToOther(tempDecimal, 2);
    }

    public static long ToDec(String sNum, int jz) throws Exception {
        long temp = 0L;
        long p = 0L;

        int num = 0;
        for (int i = sNum.length() - 1; i >= 0; --i) {
            char c = sNum.charAt(i);

            if (jz == 16) {
                if (((byte) c >= 97) && ((byte) c <= 102))
                    num = c - 'W';
                else if (((byte) c >= 65) && ((byte) c <= 70))
                    num = c - '7';
                else num = Integer.parseInt(String.valueOf(c));
            } else {
                num = Integer.parseInt(String.valueOf(c));
            }
            temp = (long) (temp + num * Math.pow(jz, p));
            p += 1L;
        }
        return temp;
    }

    public static String ToHex(String sNum, int jz) throws Exception {
        long tempDecimal = ToDec(sNum, jz);
        return DecToOther(tempDecimal, 16);
    }

    public static String ToOct(String sNum, int jz) throws Exception {
        long tempDecimal = ToDec(sNum, jz);
        return DecToOther(tempDecimal, 8);
    }

}
